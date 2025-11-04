package org.ipn.mx.among.bugs.application;

import org.ipn.mx.among.bugs.domain.entity.Player;

import java.util.List;

public interface PlayerService {
    public Player crearUsuario(Player player);
    public Player actualizarUsuario(Player player);
    public void eliminarUsuarioPorId(Long id);
    public List<Player> mostrarUsuarios();
    public Player mostrarUsuarioPorId(Long id);

}
