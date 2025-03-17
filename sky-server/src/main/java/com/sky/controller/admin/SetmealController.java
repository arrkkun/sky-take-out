package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "后台-套餐管理")
@Slf4j
public class SetmealController {


    @Autowired
    private SetmealService setmealService;


    @PostMapping
    @ApiOperation(value = "创建套餐", notes = "创建套餐")
    public Result create(@RequestBody SetmealDTO setmealDTO) {
        log.info("创建套餐");

        setmealService.create(setmealDTO);

        return Result.success();
    }


    @GetMapping("/page")
    @ApiOperation(value = "分页查询套餐", notes = "分页查询套餐")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("分页查询套餐");

        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);

        return Result.success(pageResult);
    }

    @DeleteMapping
    @ApiOperation(value = "删除套餐", notes = "删除套餐")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("删除套餐");

        setmealService.delete(ids);

        return Result.success();

    }


    @GetMapping("/{id}")
    @ApiOperation(value = "根据ID查询套餐", notes = "根据ID查询套餐")
    public Result<SetmealVO> getById(@PathVariable Long id) {
        log.info("根据ID查询套餐");

        SetmealVO setmealVO = setmealService.getByIdWithDish(id);

        return Result.success(setmealVO);

    }

    @PutMapping
    @ApiOperation(value = "修改套餐", notes = "修改套餐")
    public Result update(@RequestBody SetmealDTO setmealDTO) {
        log.info("修改套餐");

        setmealService.update(setmealDTO);

        return Result.success();
    }

    @PostMapping("/status/{stauts}")
    @ApiOperation(value = "修改套餐状态", notes = "修改套餐状态")
    public Result updateStatus(@PathVariable Integer stauts ,Long id) {
        log.info("修改套餐状态");

        setmealService.updateStatus(stauts, id);

        return Result.success();
    }


}
