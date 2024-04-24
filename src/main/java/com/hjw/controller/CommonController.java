package com.hjw.controller;

import cn.hutool.core.util.IdUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectRequest;
import com.hjw.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController
{
    // @Value("${reggie.path}")
    // private String path;

    @Resource
    private OSS oss;

    @Value("${oss.aliyun.bucket}")
    public String BUCKET;

    @Value("${oss.aliyun.domain}")
    public String DOMAIN;

    @Value("${oss.aliyun.file}")
    public String fileDir;


    // 文件上传
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file)
    {
        try
        {
            InputStream inputStream = file.getInputStream();
            // 获取上传文件名称
            String fullename = file.getOriginalFilename();
            // 截取文件扩展名
            String ext = null;
            if (fullename != null)
            {
                ext = fullename.substring(fullename.lastIndexOf("."));
            }
            // 自定义文件名称
            String uuid = IdUtil.simpleUUID();
            String fileName = uuid + ext;
            // 组合阿里云OSS上传参数  依次为 存储空间名，文件名（可以包括文件夹）,文件流
            // 注意对象存储没有文件夹概念，如果要区分文件可以再文件名加/  eg:/2021/04/16/202111222555.png
            PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET, fileDir + fileName, inputStream);
            // 上传
            oss.putObject(putObjectRequest);
            return Result.success(DOMAIN + fileDir + fileName);
        }
        catch (IOException e)
        {
            log.info("error", e);
        }
        return null;
    }

    // 文件下载
    // @GetMapping("/download")
    // public void download(String name, HttpServletResponse response)
    // {
    //     try
    //     {
    //         // 输入流，读取文件内容
    //         FileInputStream fis = new FileInputStream(path + name);
    //
    //         // 输出流，将文件写会浏览器
    //         ServletOutputStream ops = response.getOutputStream();
    //         response.setContentType("image/jpeg");
    //
    //         int len;
    //         byte[] bytes = new byte[1024];
    //         while ((len = fis.read(bytes)) != -1)
    //         {
    //             ops.write(bytes, 0, len);
    //             ops.flush();
    //         }
    //
    //         ops.close();
    //         fis.close();
    //     }
    //     catch (IOException e)
    //     {
    //         log.error(e.getMessage());
    //     }
    // }

}
