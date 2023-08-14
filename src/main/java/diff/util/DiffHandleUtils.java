package diff.util;


import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.patch.Patch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DiffHandleUtils {

    /**
     * 对比两文件的差异，返回原始文件+diff格式
     *
     * @param original 原文件内容
     * @param revised  对比文件内容
     */
    public static List<String> diffString(List<String> original, List<String> revised) {
        return diffString(original, revised, null, null);
    }

    /**
     * 对比两文件的差异，返回原始文件+diff格式
     *
     * @param original         原文件内容
     * @param revised          对比文件内容
     * @param originalFileName 原始文件名
     * @param revisedFileName  对比文件名
     */
    public static List<String> diffString(List<String> original, List<String> revised, String originalFileName, String revisedFileName) {
        originalFileName = originalFileName == null ? "原始文件" : originalFileName;
        revisedFileName = revisedFileName == null ? "对比文件" : revisedFileName;
        //两文件的不同点
        Patch<String> patch = com.github.difflib.DiffUtils.diff(original, revised);
        //生成统一的差异格式
        List<String> unifiedDiff = UnifiedDiffUtils.generateUnifiedDiff(originalFileName, revisedFileName, original, patch, 0);
        int diffCount = unifiedDiff.size();
        if (unifiedDiff.size() == 0) {
            //如果两文件没差异则插入如下
            unifiedDiff.add("--- " + originalFileName);
            unifiedDiff.add("+++ " + revisedFileName);
            unifiedDiff.add("@@ -0,0 +0,0 @@");
        } else if (unifiedDiff.size() >= 3 && !unifiedDiff.get(2).contains("@@ -1,")) {
            unifiedDiff.set(1, unifiedDiff.get(1));
            //如果第一行没变化则插入@@ -0,0 +0,0 @@
            unifiedDiff.add(2, "@@ -0,0 +0,0 @@");
        }
        //原始文件中每行前加空格
        List<String> original1 = original.stream().map(v -> " " + v).collect(Collectors.toList());
        //差异格式插入到原始文件中
        return insertOrig(original1, unifiedDiff);
    }


    /**
     * 对比两文件的差异，返回原始文件+diff格式
     *
     * @param filePathOriginal 原文件路径
     * @param filePathRevised  对比文件路径
     */
    public static List<String> diffString(String filePathOriginal, String filePathRevised) {
        //原始文件
        List<String> original = null;
        //对比文件
        List<String> revised = null;
        File originalFile = new File(filePathOriginal);
        File revisedFile = new File(filePathRevised);
        try {
            original = Files.readAllLines(originalFile.toPath());
            revised = Files.readAllLines(revisedFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return diffString(original, revised, originalFile.getName(), revisedFile.getName());
    }

    /**
     * 通过两文件的差异diff生成 html文件，打开此 html文件便可看到文件对比的明细内容
     *
     * @param diffString 调用上面 diffString方法获取到的对比结果
     * @param htmlPath   生成的html路径，如:/user/var/mbos/ent/21231/
     */
    public static void generateDiffHtml(List<String> diffString, String htmlPath) {
        StringBuilder builder = new StringBuilder();
        for (String line : diffString) {
            builder.append(escapeStr(line));
            builder.append("\n");
        }
        //githubCss来自 https://cdnjs.cloudflare.com/ajax/libs/highlight.js/10.7.1/styles/github.min.css
        String githubCss = "github.min.css";
        //diff2htmlCss来自 https://cdn.jsdelivr.net/npm/diff2html/bundles/css/diff2html.min.css
        String diff2htmlCss = "diff2html.min.css";
        //diff2htmlJs来自 https://cdn.jsdelivr.net/npm/diff2html/bundles/js/diff2html-ui.min.js
        String diff2htmlJs = "diff2html-ui.min.js";

        String template = "<!DOCTYPE html>\n" +
                "<html lang=\"en-us\">\n" +
                "  <head>\n" +
                "    <meta charset=\"utf-8\" />\n" +
                "    <link rel=\"stylesheet\" href=\"" + githubCss + "\" />\n" +
                "     <link rel=\"stylesheet\" type=\"text/css\" href=\"" + diff2htmlCss + "\" />\n" +
                "    <script type=\"text/javascript\" src=\"" + diff2htmlJs + "\"></script>\n" +
                "  </head>\n" +
                "  <script>\n" +
                "    const diffString = `\n" +
                "temp\n" +
                "`;\n" +
                "\n" +
                "\n" +
                "     document.addEventListener('DOMContentLoaded', function () {\n" +
                "      var targetElement = document.getElementById('myDiffElement');\n" +
                "      var configuration = {\n" +
                "        drawFileList: true,\n" +
                "        fileListToggle: true,\n" +
                "        fileListStartVisible: true,\n" +
                "        fileContentToggle: true,\n" +
                "        matching: 'lines',\n" +
                "        outputFormat: 'side-by-side',\n" +
                "        synchronisedScroll: true,\n" +
                "        highlight: true,\n" +
                "        renderNothingWhenEmpty: true,\n" +
                "      };\n" +
                "      var diff2htmlUi = new Diff2HtmlUI(targetElement, diffString, configuration);\n" +
                "      diff2htmlUi.draw();\n" +
                "      diff2htmlUi.highlightCode();\n" +
                "    });\n" +
                "  </script>\n" +
                "  <body>\n" +
                "    <div id=\"myDiffElement\"></div>\n" +
                "  </body>\n" +
                "</html>";
        template = template.replace("temp", builder.toString());
        FileWriter f = null; //文件读取为字符流
        //如果htmlPath文件夹不存在则创建
        File folder = new File(htmlPath);
        if (!folder.exists() && !folder.isDirectory()) {
            folder.mkdirs();
        }
        //写入diff.html
        try {
            f = new FileWriter(htmlPath + "diff.html");
            BufferedWriter buf = new BufferedWriter(f); //文件加入缓冲区
            buf.write(template); //向缓冲区写入
            buf.close(); //关闭缓冲区并将信息写入文件
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //对字符串中的特殊字符进行转义
    private static String escapeStr(String linStr) {
        //如果含有反斜杠对其转义为\\
        if (linStr.contains("\\")) {
            linStr = linStr.replaceAll("\\\\", "\\\\\\\\");
        }
        //如果含有</script>将其转义为<\/script> ，否则浏览器解析到</script>将报错
        if (linStr.contains("</script>")) {
            linStr = linStr.replaceAll("</script>", "<\\\\/script>");
        }
        //如果含有字符串模板的反引号对其转义为\`
        if (linStr.contains("`")) {
            linStr = linStr.replaceAll("`", "\\\\`");
        }
        //如果含有$符号对其转义为\$
        if (linStr.contains("$")) {
            linStr = linStr.replaceAll("\\$", "\\\\\\$");
        }
        return linStr;
    }

    //统一差异格式插入到原始文件
    public static List<String> insertOrig(List<String> original, List<String> unifiedDiff) {
        List<String> result = new ArrayList<>();
        //unifiedDiff中根据@@分割成不同行，然后加入到diffList中
        List<List<String>> diffList = new ArrayList<>();
        List<String> d = new ArrayList<>();
        for (int i = 0; i < unifiedDiff.size(); i++) {
            String u = unifiedDiff.get(i);
            if (u.startsWith("@@") && !"@@ -0,0 +0,0 @@".equals(u) && !u.contains("@@ -1,")) {
                List<String> twoList = new ArrayList<>();
                twoList.addAll(d);
                diffList.add(twoList);
                d.clear();
                d.add(u);
                continue;
            }
            if (i == unifiedDiff.size() - 1) {
                d.add(u);
                List<String> twoList = new ArrayList<>();
                twoList.addAll(d);
                diffList.add(twoList);
                d.clear();
                break;
            }
            d.add(u);
        }

        //将diffList和原始文件original插入到result，返回result
        for (int i = 0; i < diffList.size(); i++) {
            List<String> diff = diffList.get(i);
            List<String> nexDiff = i == diffList.size() - 1 ? null : diffList.get(i + 1);
            //含有@@的一行
            String simb = i == 0 ? diff.get(2) : diff.get(0);
            String nexSimb = nexDiff == null ? null : nexDiff.get(0);
            //插入到result
            insert(result, diff);
            //解析含有@@的行，得到原文件从第几行开始改变，改变了多少（即增加和减少的行）
            Map<String, Integer> map = getRowMap(simb);
            if (null != nexSimb) {
                Map<String, Integer> nexMap = getRowMap(nexSimb);
                int start = 0;
                if (map.get("orgRow") != 0) {
                    start = map.get("orgRow") + map.get("orgDel") - 1;
                }
                int end = nexMap.get("revRow") - 2;
                //插入不变的
                insert(result, getOrigList(original, start, end));
            }

            int start = (map.get("orgRow") + map.get("orgDel") - 1);
            start = start == -1 ? 0 : start;
            if (simb.contains("@@ -1,") && null == nexSimb && map.get("orgDel") != original.size()) {
                insert(result, getOrigList(original, start, original.size() - 1));
            } else if (null == nexSimb && (map.get("orgRow") + map.get("orgDel") - 1) < original.size()) {
                insert(result, getOrigList(original, start, original.size() - 1));
            }
        }
        //如果你想知道两文件有几处不同可以放开下面5行代码注释，会在文件名后显示总的不同点有几处（即预览图中的xxx different），放开注释后有一个小缺点就是如果对比的是java、js等代码文件那代码里的关键字就不会高亮颜色显示,有一点不美观。
        //int diffCount = diffList.size() - 1;
        //if (!"@@ -0,0 +0,0 @@".equals(unifiedDiff.get(2))) {
        //    diffCount = diffList.size() > 1 ? diffList.size() : 1;
        //}
        //result.set(1, result.get(1) + " ( " + diffCount + " different )");
        return result;
    }

    //将源文件中没变的内容插入result
    public static void insert(List<String> result, List<String> noChangeContent) {
        for (String ins : noChangeContent) {
            result.add(ins);
        }
    }

    //解析含有@@的行得到修改的行号删除或新增了几行
    public static Map<String, Integer> getRowMap(String str) {
        Map<String, Integer> map = new HashMap<>();
        if (str.startsWith("@@")) {
            String[] sp = str.split(" ");
            String org = sp[1];
            String[] orgSp = org.split(",");
            //源文件要删除行的行号
            map.put("orgRow", Integer.valueOf(orgSp[0].substring(1)));
            //源文件删除的行数
            map.put("orgDel", Integer.valueOf(orgSp[1]));

            String[] revSp = org.split(",");
            //对比文件要增加行的行号
            map.put("revRow", Integer.valueOf(revSp[0].substring(1)));
            map.put("revAdd", Integer.valueOf(revSp[1]));
        }
        return map;
    }

    //从原文件中获取指定的部分行
    public static List<String> getOrigList(List<String> original1, int start, int end) {
        List<String> list = new ArrayList<>();
        if (original1.size() >= 1 && start <= end && end < original1.size()) {
            for (; start <= end; start++) {
                list.add(original1.get(start));
            }
        }
        return list;
    }
}

