package com.microservice.profileservice.service;

import com.google.gson.Gson;
import com.microservice.commonservice.common.CommonException;
import com.microservice.commonservice.utils.Constant;
import com.microservice.profileservice.event.EventProducer;
import com.microservice.profileservice.model.ProfileDTO;
import com.microservice.profileservice.repository.ProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
@Slf4j
public class ProfileService {
    Gson gson = new Gson();
    @Autowired
    ProfileRepository profileRepository;
    @Autowired
    EventProducer eventProducer;
    public Flux<ProfileDTO> getAllProfile(){
        return profileRepository.findAll()
                .map(ProfileDTO::entityToDto)
                .switchIfEmpty(Mono.error(new Exception("Profile list empty")));
    }
    public Mono<Boolean> checkDuplicate(String email){
        return profileRepository.findByEmail(email)
                .flatMap(profile -> Mono.just(true))
                .switchIfEmpty(Mono.just(false));
    }
    public  Mono<ProfileDTO> createNewProfile(ProfileDTO profileDTO){
        return checkDuplicate(profileDTO.getEmail())
                .flatMap(aBoolean -> {
                    if (Boolean.TRUE.equals(aBoolean)) {
                        return  Mono.error(new CommonException("PF02", "Duplicate profile by email", HttpStatus.BAD_REQUEST));
                    }else {
                        profileDTO.setStatus(Constant.STATUS_PROFILE_PENDING);
                        return createProfile(profileDTO);
                    }
                });
    }
        public Mono<ProfileDTO> createProfile(ProfileDTO profileDTO){
            return Mono.just(profileDTO)
                    .map(ProfileDTO::dtoToEntity)
                    .flatMap(profile -> profileRepository.save(profile))
                    .map(ProfileDTO::entityToDto)
                    .doOnError(throwable -> log.error(throwable.getMessage()))
                    .doOnSuccess(dto -> {
                        if(Objects.equals(dto.getStatus(), Constant.STATUS_PROFILE_PENDING)){
                            dto.setInitialBalance((profileDTO.getInitialBalance()));
                            eventProducer.send(Constant.PROFILE_ONBOARDING_TOPIC, gson.toJson(dto)).subscribe();
                        }
                    });
        }
    public Mono<ProfileDTO> updateStatusProfile(ProfileDTO profileDTO){
        return getDetailProfileByEmail(profileDTO.getEmail())
                .map(ProfileDTO::dtoToEntity)
                .flatMap(profile -> {
                    profile.setStatus(profileDTO.getStatus());
                    return profileRepository.save(profile);
                })
                .map(ProfileDTO::entityToDto)
                .doOnError(throwable -> log.error(throwable.getMessage()));
    }
    public Mono<ProfileDTO> getDetailProfileByEmail(String email){
        return profileRepository.findByEmail(email)
                .map(ProfileDTO::entityToDto)
                .switchIfEmpty(Mono.error(new CommonException("PF03", "Profile not found", HttpStatus.NOT_FOUND)));
    }
}
