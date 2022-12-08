# java-file-diff

本项目是一个使用 [java-diff-utils](https://github.com/java-diff-utils/java-diff-utils) 和 [diff2html](https://github.com/rtfpessoa/diff2html)
 开发的一个简单 java 项目，本项目可以用于对比两个文本文件的差异，最终对比结果以 html 显示。

例如：你可以对比两个 java 文件的不同。当然你也可以对比其他 txt、js、css 等文本文件。


<img src="https://img.shields.io/badge/Language-Java-orange.svg">


## 使用
```
		//对比 D:\n1.txt和 D:\n2.txt 两个文件，获得不同点
        List<String> diffString = DiffHandleUtils.diffString("D:\\n1.txt","D:\\n2.txt");

        //在 D:\diff\ 目录下生成一个 diff.html 文件，打开便可看到两个文件的对比
        String droducDirPath = "D:\\diff\\";
        DiffHandleUtils.generateDiffHtml(diffString,droducDirPath);

        //把所需的 js和 css 从 resource 资源目录复制到 droducDirPath 目录下
        FileCoypUtils.copyfile(droducDirPath);
```

 droducDirPath 目录下打开 diff.html 文件便可得到对比结果。


## 版权和许可
该项目根据 MIT 许可条款获得许可

