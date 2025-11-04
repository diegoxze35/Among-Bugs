package org.ipn.mx.among.bugs.domain.repository;

import org.springframework.data.repository.CrudRepository;
import org.ipn.mx.among.bugs.domain.entity.Player;

public interface PlayerRepository extends CrudRepository<Player, Long> {

}
