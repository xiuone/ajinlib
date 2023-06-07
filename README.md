# Jitpack发布说明
[![](https://jitpack.io/v/com.gitee.osard/test-jitpack.svg)](https://jitpack.io/#com.gitee.osard/test-jitpack)

### 介绍


- jitpack发布是比较简单且容易的方法
- 按照jitpack示例文档，整理的jitpack发布的相关配置，以及一个项目同时发布多个组件的配置方案
- 本例为多module发布，点击上方版本图标，跳转后点击 “get it”按钮，可以查看如何引用，本例是多module所以存在下拉选选择对应module的引用方式

- [多module示例](https://gitee.com/osard/cv-lib)，比较适用与多架构，多可选依赖项目

### 发布配置

- 打开 **[Jitpack官网](https://jitpack.io/)** ，使用github账号登录，点击个人的昵称进入设置页面，如图位置需要输入码云的私人令牌：

![1](https://images.gitee.com/uploads/images/2021/0802/135749_6c6f0ea3_1021361.png "1.png")

- 打开 **[Gitee码云](https://gitee.com/)** ，进入自己的设置页面，选择私人令牌创建并记录密钥：

![2](https://images.gitee.com/uploads/images/2021/0802/135925_59761a66_1021361.png "2.png")

- 在码云创建公开项目，你的个人空间地址和项目名称决定你的依赖引入时的groupId及artifactId名；

- Android studio 创建项目并添加你需要发布的library的module模块，在library的module模块下的build.gradle添加配置：

```
    plugins {
        id 'com.android.library'
        //添加插件
        id 'maven-publish'
    }

    ...

    dependencies {
        ...
    }

    //注意放置位置，复制此段代码稍加修改即可
    afterEvaluate {
        publishing {
            publications {
                release(MavenPublication) {
                    from components.release
                    //和你的仓库地址的反写一致，此处不会修改最终发布的组件依赖名，最终发布的groupId由你的仓库地址和名称决定
                    groupId = 'com.gitee.osard.test-jitpack'
                    //单组件发布时随意填写，多组件时即为此组件的artifactId
                    artifactId = 'test-jitpack'
                }
            }
        }
    }
```

- Android **com.android.tools.build:gradle:7.0.0 及以上项目生成依赖时需要配置以下信息** 

**项目根目录创建 jitpack.yml 文件**

```
before_install:
  - sdk install java 11.0.10-open
  - sdk use java 11.0.10-open
 
jdk:
  - openjdk11
```


- 将项目上传到刚刚建好的仓库，并创建发行版，复制仓库的https地址：

![3](https://images.gitee.com/uploads/images/2021/0802/140045_cf0f35c3_1021361.png "3.png") ![4](https://images.gitee.com/uploads/images/2021/0802/140122_818b3f18_1021361.png "4.png")

- 打开 **[Jitpack官网](https://jitpack.io/)** ，输入仓库的https地址，编译项目并生成依赖：

![5](https://images.gitee.com/uploads/images/2021/0802/140206_8579bf4b_1021361.png "5.png") ![6](https://images.gitee.com/uploads/images/2021/0802/140217_533bf51c_1021361.png "6.png")

- 完成构建：

![7](https://images.gitee.com/uploads/images/2021/0802/140255_173209fe_1021361.png "7.png")

### 注意

- **gradlew和gradlew.bat脚本也需要上传，缺少时会构建失败，并提示gradle版本不一致相关的错误。**
- **项目上传时 gradle/wrapper下的2个文件也需要上传，避免构建失败。** 
- **gradle7.0.0的构建只需要增加配置文件即可**

License
-------

    Copyright 2021 mjsoftking

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
