package com.yuy.runCodeOnDocker.entity;

public enum CodeEnum {
    JAVA {
        @Override
        public String getImageName() {
            return "docker-java";
        }

        @Override
        public String[][] getCommand(String fileName) {
            return new String[][]{
                    {"javac", fileName + ".java"},
                    {"java", fileName}
            };
        }

        @Override
        public String getContainerNamePrefix() {
            return "docker-java-container-";
        }

        @Override
        public String getFileName() {
            return "JavaCodeRun";
        }

        @Override
        public String getCodeType() {
            return "JAVA";
        }
    },

    CPP {
        @Override
        public String getImageName() {
            return "docker-gcc";
        }

        @Override
        public String[][] getCommand(String fileName) {
            return new String[][] {
                    {"g++", fileName, "-o", "CppCodeRun"},
                    {"./CppCodeRun"}
            };
        }

        @Override
        public String getFileName() {
            return "CppCodeRun.cpp";
        }

        @Override
        public String getContainerNamePrefix() {
            return "docker-cpp-container-";
        }

        @Override
        public String getCodeType() {
            return "CPP";
        }
    };


    /**
     * 获得镜像名称
     * @return
     */
    public String getImageName() {
        return null;
    }

    /**
     * 获得容器前缀名
     * @return
     */
    public String getContainerNamePrefix() {
        return null;
    }

    /**
     * 获得执行代码命令
     * @return
     */
    public String[][] getCommand(String fileName) {
        return null;
    }

    /**
     * 获得文件名
     * @return
     */
    public String getFileName() {
        return null;
    }


    /**
     * 获得代码类型
     * @return
     */
    public String getCodeType() {
        return null;
    }

}
