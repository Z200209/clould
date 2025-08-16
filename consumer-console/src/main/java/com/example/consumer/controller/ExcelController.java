package com.example.consumer.controller;


import com.example.common.annotations.VerifiedUser;
import com.example.common.entity.User;
import com.example.common.utils.Response;
import com.example.consumer.service.ExcelService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Excel导入导出控制器
 */
@Slf4j
@RestController
@RequestMapping("/console/excel")
public class ExcelController {

    @Autowired
    private ExcelService excelService;

    /**
     * 上传并解析Excel文件
     *
     * @param loginUser 登录用户
     * @param file      Excel文件
     * @return 解析结果
     */
    @RequestMapping("/import")
    public Response importExcel(@VerifiedUser User loginUser,
                                @RequestParam("file") MultipartFile file) {
        // 验证用户是否登录
        if (loginUser == null) {
            log.info("未登录用户尝试上传Excel文件");
            return new Response(1002);
        }

        // 验证文件
        if (file == null || file.isEmpty()) {
            log.info("用户 {} 上传了空文件", loginUser.getId());
            return new Response(4001);
        }

        // 验证文件类型
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            log.info("用户 {} 上传了非Excel文件: {}", loginUser.getId(), fileName);
            return new Response(4001);
        }

        try {
            log.info("用户 {} 开始解析Excel文件: {}", loginUser.getId(), fileName);

            // 解析Excel文件（包含表头信息）
            Map<String, Object> result = excelService.parseExcelWithHeaders(file);

            log.info("用户 {} Excel文件解析成功，共解析 {} 行数据，{} 列",
                    loginUser.getId(), result.get("totalRows"), result.get("totalColumns"));
            return new Response(1001, result);

        } catch (Exception e) {
            log.error("用户 {} Excel文件解析失败: {}", loginUser.getId(), e.getMessage(), e);
            return new Response(4001);
        }
    }

    /**
     * 上传并解析Excel文件（简单模式，不解析表头）
     *
     * @param loginUser 登录用户
     * @param file      Excel文件
     * @return 解析结果
     */
    @PostMapping("/import/simple")
    public Response importExcelSimple(@VerifiedUser User loginUser,
                                      @RequestParam("file") MultipartFile file) {
        // 验证用户是否登录
        if (loginUser == null) {
            log.info("未登录用户尝试上传Excel文件");
            return new Response(1002);
        }

        // 验证文件
        if (file == null || file.isEmpty()) {
            log.info("用户 {} 上传了空文件", loginUser.getId());
            return new Response(4001);
        }

        // 验证文件类型
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            log.info("用户 {} 上传了非Excel文件: {}", loginUser.getId(), fileName);
            return new Response(4001);
        }

        try {
            log.info("用户 {} 开始解析Excel文件（简单模式）: {}", loginUser.getId(), fileName);

            // 解析Excel文件（简单模式）
            List<Map<String, Object>> data = excelService.parseExcel(file);

            log.info("用户 {} Excel文件解析成功，共解析 {} 行数据", loginUser.getId(), data.size());
            return new Response(1001, data);

        } catch (Exception e) {
            log.error("用户 {} Excel文件解析失败: {}", loginUser.getId(), e.getMessage(), e);
            return new Response(5001, "Excel文件解析失败: " + e.getMessage());
        }
    }

    /**
     * 导出分类表数据到Excel
     *
     * @param loginUser 登录用户
     * @param response  HTTP响应
     */
    @GetMapping("/export/type")
    public Response exportType(@VerifiedUser User loginUser,
                               HttpServletResponse response, String keyword) {
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试导出分类表数据");
            return new Response(1002);
        }

        try {
            log.info("用户 {} 开始导出分类表数据", loginUser.getId());

            // 导出分类表数据
            excelService.exportTypeToExcel(response, loginUser, keyword);

            log.info("用户 {} 分类表数据导出成功", loginUser.getId());
            return new Response(1001, "分类表数据导出成功");

        } catch (Exception e) {
            log.error("用户 {} 分类表数据导出失败: {}", loginUser.getId(), e.getMessage(), e);
            return new Response(4004);
        }
    }

    /**
     * 多线程导出多个表的数据并打包成ZIP
     *
     * @param loginUser 登录用户
     * @param response  HTTP响应
     */
    @GetMapping("/export/multiple")
    public Response exportMultipleTables(@VerifiedUser User loginUser,
                                         HttpServletResponse response, String keyword, BigInteger typeId) {
        // 验证用户是否登录
        if (loginUser == null) {
            log.warn("未登录用户尝试导出多表数据");
            return new Response(1002);
        }

        try {
            log.info("用户 {} 开始多线程导出多表数据", loginUser.getId());

            // 多线程导出多表数据并打包成ZIP
            excelService.exportMultipleTablesAsZip(response, loginUser, keyword, typeId);

            log.info("用户 {} 多表数据导出成功", loginUser.getId());
            return new Response(1001, "多表数据导出成功");

        } catch (Exception e) {
            log.error("用户 {} 多表数据导出失败: {}", loginUser.getId(), e.getMessage(), e);
            return new Response(4004);
        }
    }
}