package org.ipn.mx.among.bugs;

import org.ipn.mx.among.bugs.domain.entity.Player;
import org.ipn.mx.among.bugs.domain.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

public class AmongBugsApplication implements CommandLineRunner {
    private PlayerRepository playerRepository;
    @Autowired
    public AmongBugsApplication(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public void run(String [] args){
        Player player = Player.builder().username("Masdand").email("manueasdsaddlmanue@gmail.com").passwordHash("12837188sdhjaasdssdfdydhs71wuhsgcxshey173h").build();
        playerRepository.save(player);
    }

	public static void main(String[] args) {

        SpringApplication.run(AmongBugsApplication.class, args);


	}

}
