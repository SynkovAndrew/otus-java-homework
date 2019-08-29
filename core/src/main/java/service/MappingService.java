package service;

import domain.User;
import dto.CreateUserRequestDTO;
import dto.FindUsersResponseDTO;
import dto.UserDTO;

import java.util.List;
import java.util.stream.Collectors;

public class MappingService {
    public static User map(final CreateUserRequestDTO request) {
        return User.builder()
                .age(request.getAge())
                .name(request.getName())
                .build();
    }

    public static UserDTO map(final User user) {
        return UserDTO.builder()
                .age(user.getAge())
                .name(user.getName())
                .id(user.getId())
                .build();
    }

    public static List<UserDTO> mapAsList(final List<User> users) {
        return users.stream()
                .map(MappingService::map)
                .collect(Collectors.toList());
    }

    public static FindUsersResponseDTO mapAsResponse(final List<User> users) {
        return FindUsersResponseDTO.builder()
                .content(MappingService.mapAsList(users))
                .build();
    }
}
