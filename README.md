# AspectJ-AndroidDemo
AspectJ在Android中使用的Demo

原博主博文：[【Android】在 Android / kotlin 中搭建 AspectJ 环境（2023年，Gradle 7+可用）](https://blog.csdn.net/zengsidou/article/details/129922204)

内容如下：

1. 添加依赖库
在 根项目 的 build.gradle 中，添加 AspectJ 插件的依赖：

```
buildscript {
    dependencies {
        classpath 'org.aspectj:aspectjtools:1.9.8'
        classpath 'org.aspectj:aspectjweaver:1.9.8'
    }
}
```

在模块的 build.gradle 中，添加依赖：

```
dependencies {
    api 'org.aspectj:aspectjrt:1.9.8'
}
```

2. 添加 AspectJ 编译器（AJC）执行脚本
在根目录新建 aspectj.gradle 文件如下：

```
buildscript {
    repositories {
        maven { url 'https://maven.aliyun.com/nexus/content/groups/public/' }
        google()
        mavenCentral()
    }
}

// app 和 lib 属性不同
def variants = null
if (project.android.hasProperty("applicationVariants")) {
    variants = project.android.applicationVariants
} else if (project.android.hasProperty("libraryVariants")) {
    variants = project.android.libraryVariants
}

variants.all { variant ->
    variant.outputs.all { output ->
        def fullName = ""
        output.name.tokenize('-').eachWithIndex { token, index ->
            fullName = fullName + (index == 0 ? token : token.capitalize())
        }

        JavaCompile javaCompile = variant.javaCompileProvider.get()
        javaCompile.doLast {
            String[] javaArgs = ["-showWeaveInfo",
                                 "-1.8",
                                 "-inpath", javaCompile.destinationDir.toString(),
                                 "-aspectpath", javaCompile.classpath.asPath,
                                 "-d", javaCompile.destinationDir.toString(),
                                 "-classpath", javaCompile.classpath.asPath,
                                 "-bootclasspath", project.android.bootClasspath.join(File.pathSeparator)]
            println "ajc javaArgs: " + Arrays.toString(javaArgs)
            String[] kotlinArgs = ["-showWeaveInfo",
                                   "-1.8",
                                   "-inpath", project.buildDir.path + "/tmp/kotlin-classes/" + fullName,
                                   "-aspectpath", javaCompile.classpath.asPath,
                                   "-d", project.buildDir.path + "/tmp/kotlin-classes/" + fullName,
                                   "-classpath", javaCompile.classpath.asPath,
                                   "-bootclasspath", project.android.bootClasspath.join(
                    File.pathSeparator)]
            println "ajc kotlinArgs: " + Arrays.toString(kotlinArgs)

            def wv = configurations.create("weaving")
            dependencies {
                weaving 'org.aspectj:aspectjtools:1.9.8'
            }
            try {
                javaexec {
                    classpath = wv
                    main = "org.aspectj.tools.ajc.Main"
                    args javaArgs
                }
            } catch (Exception ignored) {
            }
            try {
                javaexec {
                    classpath = wv
                    main = "org.aspectj.tools.ajc.Main"
                    args kotlinArgs
                }
            } catch (Exception ignored) {
            }
        }
    }
}
```

3. 在模块中引入脚本
在模块的 build.gradle 文件的末尾，添加以下代码：

```
apply from: "../aspectj.gradle"
```

其中，../aspectj.gradle 表示的是父文件夹下的 aspectj.gradle 文件。如果之前脚本的文件名和文件夹有所差异，需要在这里修改为对应的文件名。

4. 使用 AspectJ
到此，AspectJ 环境已经搭建成功。
AspectJ 的使用，网络上有很多教程，这里就不赘述了。
我写了个Demo，可以看看：https://github.com/LittleFogCat/AspectJ-AndroidDemo
————————————————

                            版权声明：本文为博主原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接和本声明。
                        
原文链接：https://blog.csdn.net/zengsidou/article/details/129922204
