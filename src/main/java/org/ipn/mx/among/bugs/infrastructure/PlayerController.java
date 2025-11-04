package org.ipn.mx.among.bugs.infrastructure;

import org.ipn.mx.among.bugs.application.PlayerService;
import org.ipn.mx.among.bugs.domain.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/Player")
public class PlayerController {
    private PlayerService playerService;
    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<Player> mostrarUsuarios(){
        return playerService.mostrarUsuarios();
    }
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Player mostrarUsuarioPorId(@PathVariable Long id){
        return playerService.mostrarUsuarioPorId(id);
    }




}
