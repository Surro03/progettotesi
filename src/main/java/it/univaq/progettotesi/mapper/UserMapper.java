package it.univaq.progettotesi.mapper;


import it.univaq.progettotesi.dto.UserDTO;
import it.univaq.progettotesi.entity.Client;
import it.univaq.progettotesi.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toDto(User user) {
        UserDTO dto = new UserDTO();


        dto.setUsername(user.getUsername());
        dto.setNome(user.getName());
        dto.setCognome(user.getSurname());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        dto.setNumeroDiTelefono(user.getCellphone());
        dto.setDataNascita(java.sql.Date.valueOf(user.getBirthDate()));
        dto.setRuolo(user.getRole());
        if ("CLIENT".equals(user.getRole()) && user instanceof Client client) {
            dto.setIdCondominio(client.getBuilding().getId());
        }
        return dto;
    }
}
