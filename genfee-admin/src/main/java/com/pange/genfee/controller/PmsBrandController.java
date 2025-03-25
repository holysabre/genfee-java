package com.pange.genfee.controller;

import com.pange.genfee.common.api.CommonResult;
import com.pange.genfee.dto.PmsBrandParam;
import com.pange.genfee.model.PmsBrand;
import com.pange.genfee.service.PmsBrandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther Pange
 * @description
 * @date {2025/3/22}
 */
@RestController
@RequestMapping("/brand")
@Api(tags = "品牌管理")
public class PmsBrandController {
    @Autowired
    private PmsBrandService brandService;

    @GetMapping("/listAll")
    @ApiOperation("获取所有品牌")
    public CommonResult<List<PmsBrand>> listAll(){
        List<PmsBrand> brandList = brandService.listAll();
        return CommonResult.success(brandList);
    }

    @GetMapping("/list")
    @ApiOperation("品牌列表")
    public CommonResult<List<PmsBrand>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer showStatus,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize
    ){
        List<PmsBrand> brandList = brandService.list(keyword,showStatus,pageNum,pageSize);
        return CommonResult.success(brandList);
    }

    @PostMapping("/create")
    @ApiOperation("新增品牌")
    public CommonResult create(@Validated @RequestBody PmsBrandParam brandParam){
        int count = brandService.create(brandParam);
        if(count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @GetMapping("/{id}")
    @ApiOperation("获取品牌详情")
    public CommonResult show(@PathVariable Long id){
        PmsBrand brand = brandService.getItem(id);
        return CommonResult.success(brand);
    }

    @PutMapping("/{id}")
    @ApiOperation("修改品牌")
    public CommonResult update(@Validated @RequestBody PmsBrandParam brandParam, @PathVariable Long id){
        int count = brandService.update(id,brandParam);
        if(count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除品牌")
    public CommonResult delete(@PathVariable Long id){
        int count = brandService.delete(id);
        if(count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @PostMapping("/delete/batch")
    @ApiOperation("批量删除品牌")
    public CommonResult deleteBatch(@RequestParam List<Long> ids){
        int count = brandService.delete(ids);
        if(count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @PostMapping("/updateShowStatus/batch")
    @ApiOperation("批量修改品牌显示状态")
    public CommonResult updateShowStatusBatch(@RequestParam List<Long> ids, @RequestParam Integer showStatus){
        int count = brandService.updateShowStatus(ids,showStatus);
        if(count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @PostMapping("/updateFactoryStatus/batch")
    @ApiOperation("批量修改品牌厂家制造商状态")
    public CommonResult updateFactoryStatusBatch(@RequestParam List<Long> ids, @RequestParam Integer factoryStatus){
        int count = brandService.updateFactoryStatus(ids,factoryStatus);
        if(count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
}
