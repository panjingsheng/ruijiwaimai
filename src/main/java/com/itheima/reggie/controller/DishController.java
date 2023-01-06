package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
@Autowired
private CategoryService categoryService;
@PostMapping
    public R <String> save(@RequestBody DishDto dishDto){
dishService.saveWithFlavor(dishDto);

    return R.success("新增菜品成功");
    }


@GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){

    Page<Dish> pageInfo = new Page<>(page,pageSize);
    Page<DishDto> dishDtoPage=new Page<>(page,pageSize);

    LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.like(name!=null,Dish::getName,name);
queryWrapper.orderByDesc(Dish::getUpdateTime);
dishService.page(pageInfo,queryWrapper);

    BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
 List<DishDto> list= pageInfo.getRecords().stream().map((item)->{
     Long categoryId = item.getCategoryId();
     Category byId = categoryService.getById(categoryId);
     DishDto dishDto = new DishDto();
     BeanUtils.copyProperties(item,dishDto);
     if (byId!=null) {
         dishDto.setCategoryName(byId.getName());
     }

     return dishDto;
 }).collect(Collectors.toList());

dishDtoPage.setRecords(list);


    return R.success(dishDtoPage);
    }




@GetMapping("/{id}")
    public R<DishDto>  get(@PathVariable Long id){

    DishDto dishDto = dishService.getByIdWithFlavor(id);

    return R.success(dishDto);
}




    @PutMapping
    public R <String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);

        return R.success("更新菜品成功");
    }


/*
@GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
    LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
    queryWrapper.eq(Dish::getStatus,1);
    queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
    List<Dish> list = dishService.list(queryWrapper);
    return R.success(list);
    }
*/





    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dtoList= list.stream().map((item)->{
            Long categoryId = item.getCategoryId();
            Category byId = categoryService.getById(categoryId);
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            if (byId!=null) {
                dishDto.setCategoryName(byId.getName());
            }
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);

            List<DishFlavor> list1 = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
          dishDto.setFlavors(list1);
            return dishDto;
        }).collect(Collectors.toList());


        return R.success(dtoList);
    }

}
