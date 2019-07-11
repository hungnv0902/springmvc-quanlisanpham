package com.codegym.controller;


import com.codegym.model.Product;
import com.codegym.persistence.ProductPersistenceImpl;
import com.codegym.service.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
public class ProductController {

    @Autowired
    Environment env;

    @Autowired
    private ProductServiceImpl productService;
    @RequestMapping("/")
    public String showProduct(Model model) {
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        return "list";
    }

    @GetMapping(value = "/show-product-form")
    public ModelAndView showProductForm() {
        ModelAndView modelAndView = new ModelAndView("add");
        modelAndView.addObject("product", new Product());
        return modelAndView;
    }

    @PostMapping("save-product")
    public String save(@ModelAttribute(value = "product") Product product, BindingResult result, RedirectAttributes redirect){

        if (result.hasErrors()) {
            System.out.println("Result Error Occured" + result.getAllErrors());
        }

        // lay ten file
        MultipartFile multipartFile = product.getImages();
        String fileName = multipartFile.getOriginalFilename();


        // luu file len server
        try {
            FileCopyUtils.copy(product.getImages().getBytes(), new File(env.getProperty("file_upload") + fileName));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Product productObject = new Product(product.getId(),product.getName(),product.getPrice(),
                product.getDescription(),fileName);
        productService.save(productObject);
        redirect.addFlashAttribute("success","Saved product successfully!");
        return "redirect:/";
    }
    @GetMapping("/product-detail/{id}")
    public String viewDetail(@PathVariable int id, Model model) {
        Product product = null;

            product = productService.findById(id);
        model.addAttribute("product", product);
        return "detail";
    }

    @GetMapping(value = "/search-product")
    public String searchProduct(@RequestParam String name,Model model){
        List<Product> listSearch = productService.findByName(name);
        model.addAttribute("products",listSearch);
        return "search";

    }

    @GetMapping("/product-edit/{id}")
    public ModelAndView editProductForm(@PathVariable int id) {
        Product product = productService.findById(id);
        ModelAndView modelAndView = new ModelAndView("edit");
        modelAndView.addObject("product",product);
        return modelAndView;
    }

    @PostMapping("/edit-product")
    public String update(@ModelAttribute(value = "product") Product product, BindingResult result, RedirectAttributes redirect){

        if (result.hasErrors()) {
            System.out.println("Result Error Occured" + result.getAllErrors());
        }

        // lay ten file
        MultipartFile multipartFile = product.getImages();
        String fileName = multipartFile.getOriginalFilename();


        // luu file len server
        try {
            FileCopyUtils.copy(product.getImages().getBytes(), new File(env.getProperty("file_upload") + fileName));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        product.setAvatar(fileName);
        productService.update(product.getId(),product);
        redirect.addFlashAttribute("success", "Modified product successfully!");
        return "redirect:/";
    }

    @GetMapping("/product-delete/{id}")
    public ModelAndView deleteProductForm(@PathVariable int id) {
        Product product = productService.findById(id);
        ModelAndView modelAndView = new ModelAndView("delete");
        modelAndView.addObject("product",product);
        return modelAndView;
    }

    @PostMapping("/delete-product")
    public String deleteProduct(@ModelAttribute Product product, Model model) {
        try{
        int id = product.getId();
        productService.remove(id);
        }catch (Exception e){

        }
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        return "list";

    }

}
