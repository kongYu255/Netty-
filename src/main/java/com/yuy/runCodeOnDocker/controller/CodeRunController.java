package com.yuy.runCodeOnDocker.controller;

import com.alibaba.fastjson.JSONObject;
import com.yuy.docker.DockerUtil;
import com.yuy.entity.*;
import com.yuy.mapper.CodeMapper;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.CharsetUtil;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.FileInputStream;
import java.io.InputStream;


public class CodeRunController {

    private CodeMapper codeMapper;


    public CodeRunController() {
        InputStream mybatisConfig = null;
        try {
            mybatisConfig = new FileInputStream("config/mybatis-config.xml");
            SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(mybatisConfig);
            SqlSession sqlSession = factory.openSession(true);
            codeMapper = sqlSession.getMapper(CodeMapper.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理请求
     * @param request
     * @return
     */
    public Result handler(FullHttpRequest request) {

        Result result = null;

        if(request.method() == HttpMethod.POST && "/codeRun".equals(request.uri())) {
            CodeDto dto = JSONObject.parseObject(request.content().toString(CharsetUtil.UTF_8), CodeDto.class);
            User user = new User(dto.getUsername(), dto.getPassword());
            CodeEnum code = CodeEnum.valueOf(dto.getCodeType().toUpperCase());

            DockerUtil client = new DockerUtil();
            result = client.exec(null, code, dto.getContent(), Result.OK("ok"));
        }

        return result;
    }

    /**
     * 保存代码至数据库
     * @param request
     * @return
     */
    public Result saveCode(FullHttpRequest request) {
        Result result = Result.ERROR("保存失败！");

        if(request.method() == HttpMethod.POST && "/saveCode".equals(request.uri())) {
            CodeDto dto = JSONObject.parseObject(request.content().toString(CharsetUtil.UTF_8), CodeDto.class);
            User user = new User(dto.getUsername(), dto.getPassword());
            user.setId(dto.getMemberId());
            String code = dto.getContent();
            CodeEntity entity = new CodeEntity();
            entity.setContent(code);
            entity.setMemberId(user.getId());
            entity.setType(dto.getCodeType().toUpperCase());

            codeMapper.save(entity);
            result = Result.OK("ok");
        }
        return result;
    }

    /**
     * 从数据库中取出代码返回
     * @param request
     * @return
     */
    public Result getCode(FullHttpRequest request) {
        Result result = Result.ERROR("产生错误");

        if(request.method() == HttpMethod.GET && request.uri().startsWith("/getCode")) {
            String uri = request.uri();
            String idStr = uri.substring(uri.lastIndexOf("/") + 1);
            try {
                int memberId = Integer.parseInt(idStr);
                String code = codeMapper.getCodeByMemberId(memberId);
                result = Result.OK("ok");
                result.data("content", code);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return result;
    }


    /**
     * 判断用户密码正确与否
     * @param user
     * @return
     */
    private boolean judge(User user) {

        return true;
    }


}
