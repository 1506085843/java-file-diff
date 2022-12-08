package diff;

import diff.util.DiffHandleUtils;
import diff.util.FileCoypUtils;

import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        //对比 D:\n1.txt和 D:\n2.txt 两个文件，获得不同点
        List<String> diffString = DiffHandleUtils.diffString("D:\\n1.txt","D:\\n2.txt");

        //在 D:\diff\ 目录下生成一个 diff.html 文件，打开便可看到两个文件的对比
        String droducDirPath = "D:\\diff\\";
        DiffHandleUtils.generateDiffHtml(diffString,droducDirPath);

        //把所需的 js和 css 从 resource 资源目录复制到 droducDirPath 目录下
        FileCoypUtils.copyfile(droducDirPath);

        System.out.println("对比完成，请打开 "+droducDirPath+"diff.html 查看");
    }
}
