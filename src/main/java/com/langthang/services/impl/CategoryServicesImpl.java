package com.langthang.services.impl;

import com.langthang.dto.CategoryDTO;
import com.langthang.exception.HttpError;
import com.langthang.exception.NotFoundError;
import com.langthang.model.Category;
import com.langthang.repository.CategoryRepository;
import com.langthang.services.ICategoryServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryServicesImpl implements ICategoryServices {

    private final CategoryRepository categoryRepo;

    @Autowired
    public CategoryServicesImpl(CategoryRepository categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    @Override
    public List<CategoryDTO> getAllCategory(Pageable pageable) {
        return categoryRepo.findAll(pageable).stream()
                .map(CategoryDTO::toCategoryDTO)
                .collect(Collectors.toList());
    }


    @Override
    public void deleteCategory(int categoryId) {
        Category category = categoryRepo.findById(categoryId).orElse(null);

        if (category == null) {
            throw new NotFoundError("Category with id: " + categoryId + " not found");
        }

        categoryRepo.delete(category);
    }

    @Override
    public CategoryDTO modifyCategory(int categoryId, String newName) {
        Category category = categoryRepo.findById(categoryId).orElse(null);
        if (category == null) {
            throw new NotFoundError("Category with id: " + categoryId + " not found");
        }

        boolean isExisted = categoryRepo.existsByName(newName);
        if (isExisted) {
            throw new HttpError("Category with name: " + newName + " is already existed", HttpStatus.CONFLICT);
        }

        category.setName(newName);
        Category savedCategory = categoryRepo.save(category);

        return CategoryDTO.toCategoryDTO(savedCategory);
    }

    @Override
    public CategoryDTO addNewCategory(String categoryName) {
        boolean isCategoryExist = categoryRepo.existsByName(categoryName);
        if (isCategoryExist) {
            throw new HttpError("Category with name: " + categoryName + " is already existed",
                    HttpStatus.CONFLICT);
        }

        Category newCategory = new Category(categoryName);
        categoryRepo.save(newCategory);

        return CategoryDTO.toCategoryDTO(newCategory);
    }

}
