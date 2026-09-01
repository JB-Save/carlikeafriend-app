package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.MakeDTO;
import com.carlikeafriend_backend.backend.dto.SimpleResponseDTO;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
import com.carlikeafriend_backend.backend.service.IMakeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/carlikeafriend")
public class MakeController {

    private final IMakeService makeService;

    @Autowired
    public MakeController(IMakeService makeService){
        this.makeService = makeService;
    }

    @PostMapping("/makes")
    public ResponseEntity<SimpleResponseDTO> saveMake(@RequestBody @Valid MakeDTO makeDTO)
        throws DuplicateResourceException {
        SimpleResponseDTO savedMake = makeService.saveMake(makeDTO);
        return new ResponseEntity<>(savedMake, HttpStatus.CREATED);
    }

    @GetMapping("/makes")
    public ResponseEntity<List<SimpleResponseDTO>> getAllMakes(){
        return new ResponseEntity<>(makeService.getAllMakes(), HttpStatus.OK);
    }

    @GetMapping("/makes/{id}")
    public ResponseEntity<SimpleResponseDTO> getMakeById(@PathVariable Long id){
        Optional<SimpleResponseDTO> makeResponseDTO = makeService.getMakeById(id);
        return makeResponseDTO.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/makes/{id}")
    //@PatchMapping("/makes/{id}")
    public ResponseEntity<SimpleResponseDTO> updateMake(
            @PathVariable Long id,
            @RequestBody @Valid MakeDTO makeDTO)
        throws DuplicateResourceException {
        SimpleResponseDTO updatedMake = makeService.updateMake(id, makeDTO);
        return new ResponseEntity<>(updatedMake, HttpStatus.OK);
    }

    @DeleteMapping("/makes/{id}")
    public ResponseEntity<Void> deleteMake(@PathVariable Long id){
        makeService.deleteMake(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
