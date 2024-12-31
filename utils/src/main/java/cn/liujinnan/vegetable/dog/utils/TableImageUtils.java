package cn.liujinnan.vegetable.dog.utils;

import cn.liujinnan.vegetable.dog.utils.tableImg.AlignmentEnum;
import cn.liujinnan.vegetable.dog.utils.tableImg.Cell;
import cn.liujinnan.vegetable.dog.utils.tableImg.ImageAttribute;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import sun.font.FontDesignMetrics;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 生成图片表格
 */
public class TableImageUtils {


    /**
     * 旋转后生成图片
     * 表格1  旋转成  表格2
     * a|b|c        a|d|e
     * d|1|2  ===>  b|1|3
     * e|3|4        c|2|4
     *
     * @param title
     * @param allValue
     * @param headers
     * @return
     */
    public static BufferedImage rotateImage(Object title, List<List<Object>> allValue, List<Object> headers) {
        if (Objects.isNull(allValue) || allValue.isEmpty()) {
            return null;
        }
        List<List<Object>> table = Lists.newArrayList();
        if (Objects.nonNull(headers) && !headers.isEmpty()) {
            table.add(headers);
        }
        table.addAll(allValue);

        Map<Integer, List<Object>> colMap = Maps.newTreeMap();
        for (int i = 0; i < table.size(); i++) {
            List<Object> objects = table.get(i);
            colMap.put(i, objects);
        }
        // 翻转后的行数(包含标题行)
        int rowSize = colMap.values().stream().map(List::size).max(Integer::compareTo).orElse(0);
        if (Objects.equals(rowSize, 0)) {
            return null;
        }

        List<List<Object>> rotateTable = Lists.newArrayListWithCapacity(rowSize);
        // 初始化
        for (int i = 0; i < rowSize; i++) {
            List<Object> item = Lists.newArrayList();
            rotateTable.add(item);
        }
        colMap.forEach((key, val) -> {
            for (int i = 0; i < val.size(); i++) {
                List<Object> row = rotateTable.get(i);
                row.add(val.get(i));
            }
        });

        List<Object> newHeaders = Lists.newArrayList();
        List<List<Object>> newAllValue = Lists.newArrayList();
        for (int i = 0; i < rotateTable.size(); i++) {
            // 第一行做为新的标题
            if (Objects.nonNull(headers) && i == 0) {
                newHeaders = rotateTable.get(0);
                continue;
            }
            newAllValue.add(rotateTable.get(i));
        }

        return simpleDrawTableImage(title, newAllValue, newHeaders);
    }

    /**
     * @param title
     * @param allValue List<List<Object>> allValue: Object类型为Cell , 非Cell.class类型，使用Object.toString()生成单元格内容
     * @param headers  List<Object> headers: Object类型为Cell , 非Cell.class类型，使用Object.toString()生成单元格内容
     * @return
     */
    public static BufferedImage simpleDrawTableImage(Object title, List<List<Object>> allValue, List<Object> headers) {
        // 标题和表头允许为空
        headers = Objects.isNull(headers) ? Collections.emptyList() : headers;
        Cell titleCell = new Cell();
        titleCell.setContent("");
        titleCell.setFont(new Font("宋体", Font.BOLD, 25));
        titleCell.setAlignment(AlignmentEnum.CENTER);
        if (title instanceof String) {
            titleCell.setContent(title.toString());
        }
        if (title instanceof Cell) {
            titleCell = (Cell) title;
        }
        if (Objects.isNull(allValue) || allValue.isEmpty()) {
            return null;
        }

        List<Cell> headerCells = headers.stream().map(header -> {
            if (header instanceof Cell) {
                return (Cell) header;
            }
            Cell cell = new Cell();
            cell.setContent(Objects.nonNull(header) ? header.toString() : "");
            cell.setFont(new Font("宋体", Font.BOLD, 25));
            return cell;
        }).collect(Collectors.toList());

        List<List<Cell>> cells = allValue.stream().map(valList -> {
            return valList.stream().map(val -> {
                if (val instanceof Cell) {
                    return (Cell) val;
                }
                Cell cell = new Cell();
                cell.setContent(Objects.nonNull(val) ? val.toString() : "");
                return cell;
            }).collect(Collectors.toList());
        }).collect(Collectors.toList());
        return drawTableImage(titleCell, cells, headerCells);
    }

    public static BufferedImage drawTableImage(Cell title, List<List<Cell>> allValue, List<Cell> headers) {
        if (Objects.isNull(allValue)) {
            return null;
        }
        if (Objects.isNull(title)) {
            Cell titleCell = new Cell();
            titleCell.setContent("");
            titleCell.setFont(new Font("宋体", Font.BOLD, 20));
            titleCell.setAlignment(AlignmentEnum.CENTER);
        }
        headers = Objects.isNull(headers) ? Collections.emptyList() : headers;
        ImageAttribute attribute = getImageAttribute(title, allValue, headers);
        BufferedImage image = new BufferedImage(attribute.getImgWidth(), attribute.getImgHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        // 划线加粗
        graphics.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
        // 白色背景
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        if (StringUtils.isNotBlank(title.getContent())) {
            // 写标题。居中
            Rectangle2D stringBounds = title.getFont().getStringBounds(title.getContent(), graphics.getFontRenderContext());
            int titleStart = (int) (image.getWidth() - stringBounds.getWidth()) / 2;
            if (title.getAlignment() == AlignmentEnum.LEFT) {
                titleStart = attribute.getWidthDistance();
            }
            if (title.getAlignment() == AlignmentEnum.RIGHT) {
                titleStart = image.getWidth() - (int) stringBounds.getWidth() - attribute.getWidthDistance();
            }
            graphics.setFont(title.getFont());
            graphics.setColor(title.getColor());
            // 字体会偏移，需要计算偏移量，避免内容写出单元格外
            graphics.drawString(title.getContent(), titleStart, attribute.getHeightDistance() + FontDesignMetrics.getMetrics(title.getFont()).getAscent());
        }
        List<List<Cell>> cells = new ArrayList<>();
        if (!headers.isEmpty()) {
            cells.add(headers);
        }
        cells.addAll(allValue);
        // 写表格内容
        int row = 0;
        for (List<Cell> item : cells) {
            int startX = attribute.getWidthDistance();
            // 高度按行偏移
            int startY = attribute.getHeightDistance() + attribute.getCellHeight() * (row + 1);
            row++;
            for (int i = 0; i < attribute.getTableColNum(); i++) {
                // 画单元格
                graphics.setColor(Color.BLACK);
                graphics.drawRect(startX, startY, attribute.getCellWidth(), attribute.getCellHeight());
                if (item.size() > i) {
                    Cell cell = item.get(i);
                    graphics.setFont(cell.getFont());
                    graphics.setColor(cell.getColor());
                    FontDesignMetrics metrics = FontDesignMetrics.getMetrics(cell.getFont());
                    // 对齐方式
                    Rectangle2D cellBound = cell.getFont().getStringBounds(cell.getContent(), graphics.getFontRenderContext());
                    int tmpStartX = startX;
                    if (cell.getAlignment() == AlignmentEnum.LEFT) {
                        tmpStartX = tmpStartX + attribute.getWidthFontDistance();
                    }
                    if (cell.getAlignment() == AlignmentEnum.CENTER) {
                        tmpStartX = tmpStartX + ((attribute.getCellWidth() - (int) cellBound.getWidth()) / 2);
                    }
                    if (cell.getAlignment() == AlignmentEnum.RIGHT) {
                        tmpStartX = tmpStartX + (attribute.getCellWidth() - (int) cellBound.getWidth()) - attribute.getWidthFontDistance();
                    }
                    // 往单元格写内容
                    graphics.drawString(cell.getContent(), tmpStartX,
                            startY + attribute.getHeightFontDistance() + metrics.getAscent());
                }
                startX += attribute.getCellWidth();
            }
        }
        // 消除锯齿
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        Stroke s = new BasicStroke(image.getWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
        graphics.setStroke(s);
        graphics.drawImage(image.getScaledInstance(image.getWidth(), image.getHeight(), Image.SCALE_SMOOTH), 0, 0, null);

        return image;
    }

    /**
     * 获取图片的属性
     *
     * @return
     */
    private static ImageAttribute getImageAttribute(Cell title, List<List<Cell>> allValue, List<Cell> headers) {
        BufferedImage image = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        ImageAttribute attribute = new ImageAttribute();

        List<Integer> widths = new ArrayList<>();
        List<Integer> heights = new ArrayList<>();
        headers.forEach(e -> {
            Rectangle2D stringBounds = e.getFont().getStringBounds(e.getContent(), graphics.getFontRenderContext());
            widths.add((int) stringBounds.getWidth());
            heights.add((int) stringBounds.getHeight());
        });
        allValue.forEach(list -> {
            list.forEach(e -> {
                Rectangle2D stringBounds = e.getFont().getStringBounds(e.getContent(), graphics.getFontRenderContext());
                widths.add((int) stringBounds.getWidth());
                heights.add((int) stringBounds.getHeight());
            });
        });
        List<List<Cell>> tmpList = Lists.newArrayList();
        tmpList.add(headers);
        tmpList.addAll(allValue);
        Integer colNum = tmpList.stream().map(List::size).max(Integer::compareTo).orElse(0);
        attribute.setTableColNum(colNum);
        attribute.setTableRowNum(headers.isEmpty() ? allValue.size() : allValue.size() + 1);
        // 预留像素，避免字体与单元格界线重合
        int widthFontDistance = 2;
        int heightFontDistance = 5;
        attribute.setWidthFontDistance(widthFontDistance);
        attribute.setHeightFontDistance(heightFontDistance);
        int cellWidth = widths.stream().max(Integer::compareTo).orElse(0) + 2 * widthFontDistance + 20;
        int cellHeight = heights.stream().max(Integer::compareTo).orElse(0) + 2 * heightFontDistance;
        attribute.setCellWidth(cellWidth);
        attribute.setCellHeight(cellHeight);
        int widthDistance = 40;
        int heightDistance = 30;
        int imgWidth = cellWidth * colNum + 2 * widthDistance;
        // 标题多预留一行
        FontDesignMetrics metrics = FontDesignMetrics.getMetrics(title.getFont());
        int imgHeight = cellHeight * attribute.getTableRowNum() + 3 * heightDistance + metrics.getAscent();
        attribute.setImgWidth(imgWidth);
        attribute.setImgHeight(imgHeight);
        attribute.setWidthDistance(widthDistance);
        attribute.setHeightDistance(heightDistance);
        return attribute;
    }
}
