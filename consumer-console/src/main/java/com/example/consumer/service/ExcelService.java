package com.example.consumer.service;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.example.common.dto.GameExcelDTO;
import com.example.common.dto.TagExcelDTO;
import com.example.common.dto.TypeExcelDTO;
import com.example.common.entity.Game;
import com.example.common.entity.Type;
import com.example.common.entity.User;
import com.example.consumer.feign.ConsoleGameServiceFeign;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Excel服务类
 */
@Slf4j
@Service
public class ExcelService {


    @Autowired
    private ConsoleGameServiceFeign gameService;

    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    /**
     * 解析Excel文件 - 动态识别表头
     */
    public List<Map<String, Object>> parseExcel(MultipartFile file) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            EasyExcel.read(file.getInputStream(), new PageReadListener<Map<Integer, String>>(dataList -> {
                for (Map<Integer, String> data : dataList) {
                    Map<String, Object> row = new HashMap<>();
                    for (Map.Entry<Integer, String> entry : data.entrySet()) {
                        String columnName = "column" + (entry.getKey() + 1); // 列索引从0开始，所以+1
                        row.put(columnName, entry.getValue());
                    }
                    result.add(row);
                }
            })).sheet().headRowNumber(1).doRead();  // 从第0行开始读取（包含表头）
        } catch (IOException e) {
            log.error("解析Excel文件失败", e);
            throw new RuntimeException("解析Excel文件失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 解析Excel文件 - 包含表头信息
     */
    public Map<String, Object> parseExcelWithHeaders(MultipartFile file) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<String> headers = new ArrayList<>();
        final boolean[] isFirstRow = {true}; // 使用数组来在lambda中修改值  isFirstRow 是一个布尔值，用于判断当前行是否是表头行

        try {
            // 使用EasyExcel的无模型读取方式，从第0行开始读取（包含表头）
            EasyExcel.read(file.getInputStream(), new PageReadListener<Map<Integer, String>>(dataList -> {
                for (Map<Integer, String> rowData : dataList) {
                    if (isFirstRow[0]) {
                        // 第一行作为表头
                        isFirstRow[0] = false;  // 表头行只处理一次

                        // 按列索引排序，确保表头顺序正确
                        List<Map.Entry<Integer, String>> sortedEntries = new ArrayList<>(rowData.entrySet());
                        sortedEntries.sort(Map.Entry.comparingByKey());

                        for (Map.Entry<Integer, String> entry : sortedEntries) {
                            String headerName = entry.getValue();
                            if (headerName == null || headerName.trim().isEmpty()) {
                                headerName = "column" + (entry.getKey() + 1);
                            }
                            headers.add(headerName.trim());
                        }

                        log.info("识别到表头: {}", headers);
                    } else {
                        // 数据行
                        Map<String, Object> row = new HashMap<>();

                        // 按列索引排序，确保数据顺序正确
                        List<Map.Entry<Integer, String>> sortedEntries = new ArrayList<>(rowData.entrySet());
                        sortedEntries.sort(Map.Entry.comparingByKey());

                        for (Map.Entry<Integer, String> entry : sortedEntries) {
                            int columnIndex = entry.getKey();
                            String columnName;

                            if (columnIndex < headers.size()) {
                                columnName = headers.get(columnIndex);
                            } else {
                                columnName = "column" + (columnIndex + 1);
                                // 如果数据列数超过表头列数，动态添加表头
                                while (headers.size() <= columnIndex) {
                                    headers.add("column" + (headers.size() + 1));
                                }
                            }

                            row.put(columnName, entry.getValue());
                        }
                        result.add(row);
                    }
                }
            })).sheet().headRowNumber(0).doRead();
        } catch (Exception e) {
            log.error("解析Excel文件失败", e);
            throw new RuntimeException("解析Excel文件失败: " + e.getMessage());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("headers", headers);
        response.put("data", result);
        response.put("totalRows", result.size());
        response.put("totalColumns", headers.size());

        log.info("Excel解析完成，表头: {}，数据行数: {}，列数: {}", headers, result.size(), headers.size());

        return response;
    }

    /**
     * 导出分类表数据到Excel
     */
    public void exportTypeToExcel(HttpServletResponse response, User loginUser, String keyword) {
        try {
            // 获取分类数据
            List<Type> typeList = gameService.typeList(keyword);

            if (typeList == null || typeList.isEmpty()) {
                throw new RuntimeException("分类数据为空");
            }
            List<TypeExcelDTO> excelData = new ArrayList<>();
            for (Type type : typeList) {
                TypeExcelDTO dto = new TypeExcelDTO();
                BeanUtils.copyProperties(type, dto);
                excelData.add(dto);
            }

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");  // Excel文件类型
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("分类表数据", StandardCharsets.UTF_8).replaceAll("\\+", "%20");  // 处理文件名编码
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx"); // 设置响应头

            // 写入Excel
            EasyExcel.write(response.getOutputStream(), TypeExcelDTO.class).sheet("分类数据").doWrite(excelData);

        } catch (Exception e) {
            log.error("导出分类表数据失败", e);
            throw new RuntimeException("导出分类表数据失败: " + e.getMessage());
        }
    }

    /**
     * 多线程导出多个表的数据并打包成ZIP
     * 使用CountDownLatch实现线程同步，等待所有Excel生成任务完成
     */
    public void exportMultipleTablesAsZip(HttpServletResponse response, User loginUser, String keyword, BigInteger typeId) {
        try {
            // 设置响应头
            response.setContentType("application/zip");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("数据导出", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".zip");

            try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {

                // 创建CountDownLatch，等待3个任务完成
                CountDownLatch latch = new CountDownLatch(3);

                // 用于存储各表的Excel数据
                final byte[][] results = new byte[3][];

                // 异步生成分类表Excel
                executorService.submit(() -> {
                    try {
                        results[0] = generateTypeExcel(loginUser);
                    } catch (Exception e) {
                        log.error("生成分类Excel失败", e);
                        results[0] = new byte[0];
                    } finally {
                        latch.countDown(); // 任务完成，计数器减1
                    }
                });

                // 异步生成游戏表Excel
                executorService.submit(() -> {
                    try {
                        results[1] = generateGameExcel(keyword, typeId);
                    } catch (Exception e) {
                        log.error("生成游戏Excel失败", e);
                        results[1] = new byte[0];
                    } finally {
                        latch.countDown(); // 任务完成，计数器减1
                    }
                });

                // 异步生成标签表Excel
                executorService.submit(() -> {
                    try {
                        results[2] = generateTagExcel();
                    } catch (Exception e) {
                        log.error("生成标签Excel失败", e);
                        results[2] = new byte[0];
                    } finally {
                        latch.countDown(); // 任务完成，计数器减1
                    }
                });

                // 等待所有任务完成
                latch.await();

                // 添加分类表Excel到ZIP
                if (results[0].length > 0) {
                    zipOut.putNextEntry(new ZipEntry("分类表数据.xlsx"));
                    zipOut.write(results[0]);
                    zipOut.closeEntry();
                }

                // 添加游戏表Excel到ZIP
                if (results[1].length > 0) {
                    zipOut.putNextEntry(new ZipEntry("游戏表数据.xlsx"));
                    zipOut.write(results[1]);
                    zipOut.closeEntry();
                }

                // 添加标签表Excel到ZIP
                if (results[2].length > 0) {
                    zipOut.putNextEntry(new ZipEntry("标签表数据.xlsx"));
                    zipOut.write(results[2]);
                    zipOut.closeEntry();
                }

                zipOut.finish();
            }

        } catch (Exception e) {
            log.error("导出多表数据ZIP失败", e);
            throw new RuntimeException("导出多表数据ZIP失败: " + e.getMessage());
        }
    }

    /**
     * 生成分类表Excel字节数组
     */
    private byte[] generateTypeExcel(User loginUser) throws IOException {
        return generateTypeExcel(loginUser, null);
    }

    /**
     * 生成分类表Excel字节数组（带关键词）
     */
    private byte[] generateTypeExcel(User loginUser, String keyword) throws IOException {
        List<Type> typeList = gameService.typeList(keyword);
        if (typeList == null) {
            throw new RuntimeException("分类数据为空");
        }
        List<TypeExcelDTO> excelData = new ArrayList<>();
        for (Type type : typeList) {
            TypeExcelDTO dto = new TypeExcelDTO();
            BeanUtils.copyProperties(type, dto);
            excelData.add(dto);
        }
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            EasyExcel.write(outputStream, TypeExcelDTO.class).sheet("分类数据").doWrite(excelData);
            return outputStream.toByteArray();
        }
    }

    /**
     * 生成游戏表Excel字节数组
     */
    private byte[] generateGameExcel(String keyword, BigInteger typeId) throws IOException {
        // 获取所有游戏数据，传入合适的分页参数

        List<Game> GameList = gameService.gameList(1, keyword, typeId);

        if (GameList == null) {
            log.warn("游戏列表数据为空");
            return new byte[0];
        }
        List<GameExcelDTO> excelData = new ArrayList<>();

        for (Game game : GameList) {
            GameExcelDTO dto = new GameExcelDTO();
            BeanUtils.copyProperties(game, dto);
            excelData.add(dto);
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            EasyExcel.write(outputStream, GameExcelDTO.class).sheet("游戏数据").doWrite(excelData);
            return outputStream.toByteArray();
        }
    }

    /**
     * 生成标签表Excel字节数组
     * 注意：由于标签数据通过GameController获取，这里暂时生成空的Excel文件
     * 实际项目中可以通过获取所有游戏数据，然后提取其中的标签信息来实现
     */
    private byte[] generateTagExcel() throws IOException {
        // 创建空的标签数据列表
        List<TagExcelDTO> excelData = new ArrayList<>();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            EasyExcel.write(outputStream, TagExcelDTO.class).sheet("标签数据").doWrite(excelData);
            return outputStream.toByteArray();
        }
    }
}