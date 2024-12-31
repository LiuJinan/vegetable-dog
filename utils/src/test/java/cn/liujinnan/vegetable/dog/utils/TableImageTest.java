package cn.liujinnan.vegetable.dog.utils;



import cn.liujinnan.vegetable.dog.utils.tableImg.Cell;
import cn.liujinnan.vegetable.dog.utils.tableImg.CellBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class TableImageTest {

    public static void main(String[] args) throws Exception {
//        List<Object> headers = Arrays.asList("姓名", "性别", "年龄", "学历", "身高");
//        List<List<Object>> allValue = new ArrayList<>();
//        allValue.add(Arrays.asList("张三", "男", "20", "小学", "175"));
//        allValue.add(Arrays.asList("李四", "男", "21", "高中", "170"));
//        allValue.add(Arrays.asList("王红", "女", "24", "本科", "165"));
//        allValue.add(Arrays.asList("石破天", "男", "25", "本科"));
//        allValue.add(Arrays.asList("刘一刀", "男", "26", "本科", "180"));
//        BufferedImage img = TableImageUtils.simpleDrawTableImage("标题", allValue, headers);
//        ImageIO.write(img, "jpg", new File("1.jpg"));

//        List<Object> headers = Arrays.asList("", "百分比");
//        List<List<Object>> allValue = new ArrayList<>();
//        allValue.add(Arrays.asList("注册波动率", "15"));
//        BufferedImage img = TableImageUtils.simpleDrawTableImage("注册波动率", allValue, headers);
//        ImageIO.write(img, "jpg", new File("2.jpg"));

        List<Object> headers = Arrays.asList("名称", "累计", "当天累计");
        List<List<Object>> allValue = new ArrayList<>();
        allValue.add(Arrays.asList("注册人数", "318", "2470"));
        allValue.add(Arrays.asList("申请人数", "136", "840"));

        Cell a = CellBuilder.builder("¥13000").colorRed().center().build();
        allValue.add(Arrays.asList("授信人数", "16", "90"));
        allValue.add(Arrays.asList("授信金额", "¥57000", "¥394500"));
        allValue.add(Arrays.asList("提现笔数", "19", "124"));
        allValue.add(Arrays.asList("放款笔数", "5", "34"));
        allValue.add(Arrays.asList("放款率", "19%", "30%"));
        allValue.add(Arrays.asList("放款金额", a, "¥117000"));
        BufferedImage img = TableImageUtils.rotateImage("汇总数据", allValue, headers);
//        BufferedImage img = TableImageUtils.simpleDrawTableImage("汇总数据", allValue, null);
        ImageIO.write(img, "jpg", new File("4.jpg"));
    }
}
