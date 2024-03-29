package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;


    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }


    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
Page<Setmeal> pageInfo=new Page<>(page,pageSize);
Page<SetmealDto> dtoPage=new Page<>();
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo,queryWrapper);

        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> setmealDtos=     records.stream().map((item)->{
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            Long categoryId = item.getCategoryId();

            Category category = categoryService.getById(categoryId);
            if (category!=null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());
dtoPage.setRecords(setmealDtos);
        return R.success(dtoPage);
    }



@DeleteMapping
public R<String> delete(@RequestParam List<Long> ids){
setmealService.removeWithDish(ids);

        return R.success("套餐数据删除成功");
}

@GetMapping("/list")
    public R<List<Setmeal>> list(@RequestBody Setmeal setmeal){
    LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
    queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
    queryWrapper.orderByDesc(Setmeal::getUpdateTime);

    List<Setmeal> list = setmealService.list(queryWrapper);

    return R.success(list);


}



}
