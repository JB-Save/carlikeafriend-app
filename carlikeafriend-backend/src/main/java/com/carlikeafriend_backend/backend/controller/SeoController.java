package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.ProductResponseDTO;
import com.carlikeafriend_backend.backend.service.IProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class SeoController {

    private final IProductService productService;

    @Value("${frontend.base-url}")
    private String frontendBaseUrl;

    @Value("${backend.base-url}")
    private String backendBaseUrl;

    @Autowired
    public SeoController(IProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/product-details/{id}")
    public String getProductPage(@PathVariable Long id,
                                 @RequestParam(required = false) Long branchId,
                                 HttpServletRequest request,
                                 Model model) {

        String userAgent = request.getHeader("User-Agent");

        // Detectar si es un bot de redes sociales
        if (isSocialMediaBot(userAgent)) {
            Optional<ProductResponseDTO> productOpt = productService.getProductById(id);

            if (productOpt.isPresent()) {
                // ESCENARIO IDEAL: El producto existe, pasamos los datos
                ProductResponseDTO product = productOpt.get();
                // Pasamos los datos al HTML que verá el bot
                model.addAttribute("title", "Renta un " + product.getName());
                model.addAttribute("description", product.getDescription());

                if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
                    model.addAttribute("imageUrl", backendBaseUrl + "/carlikeafriend/products/images" + product.getProductImages().get(0).getImagePath());
                } else {
                    model.addAttribute("imageUrl", backendBaseUrl + "/carlikeafriend/products/images/image/product_folder/498c000a-9611-4b9c-a07c-ea12cdc86f16_default-car.png"); // Imagen por defecto
                }

                model.addAttribute("url", request.getRequestURL().toString() + (branchId != null ? "?branchId=" + branchId : ""));

            } else {
                // ESCENARIO DE FALLO: El producto fue borrado o no existe
                // Le pasamos metadatos genéricos a la plantilla
                model.addAttribute("title", "Car Like A Friend - Renta de Vehículos");
                model.addAttribute("description", "El vehículo que buscas ya no está disponible, pero tenemos excelentes alternativas para ti.");
                model.addAttribute("imageUrl", backendBaseUrl + "/carlikeafriend/products/images/image/product_folder/498c000a-9611-4b9c-a07c-ea12cdc86f16_default-car.png");
                model.addAttribute("url", frontendBaseUrl);
            }
            return "share/bot-preview"; // Un archivo HTML simple en src/main/resources/templates
        }
        // Si es un usuario real, servimos el index.html normal de React
        return "forward:/index.html";
    }

    private boolean isSocialMediaBot(String userAgent) {
        if (userAgent == null) return false;
        String ua = userAgent.toLowerCase();
        return ua.contains("facebookexternalhit") ||
                ua.contains("twitterbot") ||
                ua.contains("whatsapp") ||
                ua.contains("linkedinbot") ||
                ua.contains("telegrambot") ||
                ua.contains("instagram");
    }
}
