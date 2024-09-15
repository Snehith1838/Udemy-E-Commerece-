package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.CategoryRepository;
import com.ecommerce.project.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImplementation implements ProductService{

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ModelMapper modelMapper;



    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("category","categoryId",categoryId));

        List<Product> products = category.getProducts();
        for(Product product: products){
            if(product.getProductName().equalsIgnoreCase(productDTO.getProductName())){
                throw new APIException("this product is already exist in this " + category.getCategoryName() + " category");
            }
        }
        Product product = modelMapper.map(productDTO, Product.class);
        product.setCategory(category);
        product.setImage("default.png");
        double specialPrice = product.getPrice() -
                ((product.getDiscount() * 0.01) * product.getPrice());
        product.setSpecialPrice(specialPrice);
        Product savedProduct = productRepository.save(product);

        return modelMapper.map(savedProduct, ProductDTO.class);
    }




    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortBy.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findAll(pageDetails);

        List<Product> products = productPage.getContent();
        if(products.isEmpty()){
            throw new APIException("No products available!!");
        }
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }




    @Override
    public ProductResponse getProductByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException("category","categoryId",categoryId));

        Sort sortByAndOrder = sortBy.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findByCategoryOrderByPriceAsc(category, pageDetails);

        List<Product> products = productPage.getContent();
        if(products.isEmpty()){
            throw new APIException("No products available in this " + category.getCategoryName() + " category!!");
        }
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;

    }




    @Override
    public ProductResponse getProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortBy.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findByProductNameLikeIgnoreCase("%" + keyword + "%", pageDetails);
        List<Product> products = productPage.getContent();
        if(products.isEmpty()){
            throw new APIException("no products available with this keyword : " + keyword );
        }
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }




    @Override
    public ProductDTO updateProduct(Long productId,ProductDTO productDTO) {
        Product  product= modelMapper.map(productDTO, Product.class);
        Product savedProduct = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product","productId",productId));

        savedProduct.setProductName(product.getProductName());
        savedProduct.setDescription(product.getDescription());
        savedProduct.setQuantity(product.getQuantity());
        savedProduct.setPrice(product.getPrice());
        savedProduct.setDiscount(product.getDiscount());
        double specialPrice = product.getPrice() -
                ((product.getDiscount() * 0.01) * product.getPrice());
        savedProduct.setSpecialPrice(specialPrice);

        Product productSaved = productRepository.save(savedProduct);

        List<Cart> carts = cartRepository.findCartsByProductId(productId);

        List<CartDTO> cartDTOS = carts.stream().map(cart->{
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

            List<ProductDTO> products = cart.getCartItems().stream()
                    .map(p-> modelMapper.map(p.getProduct(), ProductDTO.class))
                    .toList();
            cartDTO.setProducts(products);

            return cartDTO;
        }).toList();

        cartDTOS.forEach(cart-> cartService.updateProductInCarts(cart.getCartId(), productId));

        return modelMapper.map(productSaved, ProductDTO.class);
    }



    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product","productId",productId));

        //deleting product in carts
        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        carts.forEach(cart-> cartService.deleteProductFromCart(cart.getCartId(), productId));

        productRepository.delete(product);
        ProductDTO deletedProductDTO = modelMapper.map(product, ProductDTO.class);
        return deletedProductDTO;
    }
}
