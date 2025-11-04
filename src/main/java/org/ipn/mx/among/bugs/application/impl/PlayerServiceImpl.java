package org.ipn.mx.among.bugs.application.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.ipn.mx.among.bugs.application.PlayerService;
import org.ipn.mx.among.bugs.domain.entity.Player;
import org.ipn.mx.among.bugs.domain.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService {
    private PlayerRepository dao;
    @Autowired
    public PlayerServiceImpl(PlayerRepository dao) {
        this.dao = dao;
    }
    @Override
    @Transactional
    public Player crearUsuario(Player player){
        return dao.save(player);
    }
    @Override
    public Player actualizarUsuario(Player player){
        return dao.save(player);
    }
    @Override
    public void eliminarUsuarioPorId(Long id){
        dao.deleteById(id);
    }
    @Override
    @Transactional(readOnly=true)
    public List<Player> mostrarUsuarios(){
        return (List<Player>)dao.findAll();
    }
    @Override
    @Transactional(readOnly=true)
    public Player mostrarUsuarioPorId(Long id){
        return dao.findById(id).orElse(null);
    }
}
