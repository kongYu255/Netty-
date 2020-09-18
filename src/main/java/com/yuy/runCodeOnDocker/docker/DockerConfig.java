package com.yuy.runCodeOnDocker.docker;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;


public class DockerConfig {

    // docker主机地址
    public static String DOCKER_HOST;
    // docker存放证书的目录
    public static String DOCKER_CERTS_PATH;
    // 工作文件夹
    public static String DOCKER_CONTAINER_WORK_DIR = "/usr/src/app";

    public static String DOCKER_REGISTER_URL;
    public static String DOCKER_API_VERSION;

    static {
        try {
            InputStream in = new FileInputStream("config/docker.properties");
            ResourceBundle resource = new PropertyResourceBundle(in);

            DOCKER_HOST = resource.getString("dockerHost");
            DOCKER_CERTS_PATH = resource.getString("dockerCertPath");
            DOCKER_REGISTER_URL = resource.getString("dockerRegisterUrl");
            DOCKER_API_VERSION = resource.getString("dockerApiVersion");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
