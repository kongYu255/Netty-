package com.yuy.runCodeOnDocker.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.DockerCmdExecFactory;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.github.dockerjava.jaxrs.JerseyDockerCmdExecFactory;
import com.yuy.entity.CodeEnum;
import com.yuy.entity.Result;
import com.yuy.entity.User;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DockerUtil {

    private User user;

    /**
     * 获取一个Docker客户端
     * @return
     */
    private DockerClient getDockerClient() {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerTlsVerify(true)
                .withDockerCertPath(DockerConfig.DOCKER_CERTS_PATH)
                .withDockerConfig(DockerConfig.DOCKER_CERTS_PATH)
                .withDockerHost(DockerConfig.DOCKER_HOST)
                .withApiVersion(DockerConfig.DOCKER_API_VERSION)
                .withRegistryUrl(DockerConfig.DOCKER_REGISTER_URL)
                .build();

        DockerCmdExecFactory dockerCmdExecFactory = new JerseyDockerCmdExecFactory()
                .withReadTimeout(1000)
                .withConnectTimeout(1000);

        DockerClient dockerClient = DockerClientBuilder.getInstance(config)
                .withDockerCmdExecFactory(dockerCmdExecFactory)
                .build();

        return dockerClient;
    }

    /**
     * 创建运行代码的容器
     * @param dockerClient
     * @param code
     * @return
     */
    private String createContainer(DockerClient dockerClient, CodeEnum code){
        // 创建容器请求
        CreateContainerResponse containerResponse = dockerClient.createContainerCmd(code.getImageName())
                .withName(code.getContainerNamePrefix() + UUID.randomUUID().toString())
                .withWorkingDir(DockerConfig.DOCKER_CONTAINER_WORK_DIR)
                .withStdinOpen(true)
                .exec();

        return containerResponse.getId();
    }

    /**
     * 把代码写入容器中的文件，后续通过命令编译运行
     * @param client
     * @param containId
     * @param code
     * @param codeContent
     * @return
     * @throws Exception
     */
    private String writeFileToContainer(DockerClient client, String containId, CodeEnum code, String codeContent) throws Exception {
        String workDir = DockerConfig.DOCKER_CONTAINER_WORK_DIR;
        // 文件名后缀加上.java，编译时候需要用到
        String fileName = code.getFileName();
        // 路径，把代码写入docker中的工作文件夹中用于编译运行
        String path = workDir + "/" + fileName;
        if(code.getCodeType().equals("JAVA")) {
            path += ".java";
        }
        // 代码
        String sourcecode = codeContent;

        // 将\替换为\\\\，转义反斜杠
        sourcecode = sourcecode.replaceAll("\\\\", "\\\\\\\\\\\\\\\\");

        // 将"替换为\"，转义引号
        sourcecode = sourcecode.replaceAll("\\\"", "\\\\\"");

        // 用shell命令把代码写入文件中
        ExecCreateCmdResponse cmdResponse = client.execCreateCmd(containId)
                .withCmd("/bin/sh","-c", "echo \""+sourcecode+"\" > "+path)
                .exec();
        client.execStartCmd(cmdResponse.getId())
                .exec(new ExecStartResultCallback(System.out,System.err))
                .awaitCompletion();

        return fileName;
    }


    /**
     * 执行代码
     * @param client
     * @param command
     * @param containerId
     * @param result
     * @throws InterruptedException
     */
    private void runCommandOnContainer(ChannelHandlerContext ctx, DockerClient client, String[] command, String containerId, Result result) throws InterruptedException {
        ExecCreateCmdResponse cmdResponse = client.execCreateCmd(containerId)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withCmd(command)
                .exec();

        client.execStartCmd(cmdResponse.getId())
                .exec(new RunCodeResultCallback(result, ctx))
                .awaitCompletion(1000, TimeUnit.SECONDS);

    }



    /**
     * 通过Docker容器运行代码，并返回结果
     * @param code
     * @param codeContent
     * @param result
     * @return
     */
    public Result exec(ChannelHandlerContext ctx, CodeEnum code, String codeContent, Result result) {

        DockerClient client = getDockerClient();

        // 创建容器
        String containerId = createContainer(client, code);
        client.startContainerCmd(containerId).exec();

        try {
            // 写入文件夹
            writeFileToContainer(client, containerId, code, codeContent);
            String[][] commands = code.getCommand(code.getFileName());

            // docker中一次只能执行一条命令，java8编译和运行要分两次运行
            for (int i = 0; i < commands.length; i++) {
                runCommandOnContainer(ctx, client, commands[i], containerId, result);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Result.ERROR("代码运行产生了错误");
        } finally {
            // 关闭并移除容器
            client.killContainerCmd(containerId).exec();
            client.removeContainerCmd(containerId).exec();
        }

        return result;

    }

}
