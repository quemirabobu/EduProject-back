package com.bit.eduventure.User.Service;


import com.bit.eduventure.User.DTO.UserDTO;
import com.bit.eduventure.User.Entity.User;
import com.bit.eduventure.User.Repository.UserRepository;
import com.bit.eduventure.Email.Service.EmailService;
import com.bit.eduventure.nchat.service.NchatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final NchatService nchatService;
    private final PasswordEncoder passwordEncoder;
    private  final EmailService emailService;
    @Override
    public User idCheck(String userId) {
        Optional<User> userOptional = userRepository.findByUserId(userId);

        //아이디가 중복되지 않았으면 null 리턴
        if(userOptional.isEmpty()) {
            return null;

        }

        //아이디가 중복됐으면 User엔티티 리턴
        return userOptional.get();
    }

    @Override
    public User join(User user) {
        if(user == null || user.getUserId() == null) {
            throw new RuntimeException("invalid argument");
        }
        if(userRepository.existsByUserId(user.getUserId())) {
            throw new RuntimeException("already exist id");
        }
        try{
            if(user.getUserType().equals("student")) {
                emailService.sendHtmlMessage(user.getUserId(), "에듀우-벤처러부터 온 메세지라우! 환영합니다 ^_^ 당신은 학생이구만", "교육계의 혁신 에듀우-벤처에 오신걸 환영합니다. \n 당신은 우리의 일원입니다 이제 \n 당신은 공부라는 지옥에 빠졋다" );
            }else {
                emailService.sendHtmlMessage(user.getUserId(), "에듀우-벤처러부터 온 메세지라우! 환영합니다 ^_^ 당신은 부모구만", "교육계의 혁신 에듀우-벤처에 오신걸 환영합니다. \n 당신은 우리의 일원입니다 이제 \n 자식키우기 쉽지 않겟구만!");
            }
            nchatService.nChatJoin(user.EntityToDTO());
        }catch (Exception e){
            System.out.println(e);
        }
        return userRepository.save(user);
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public User updateUser(User user, UserDTO userDTO) {
        if (StringUtils.hasText(userDTO.getApproval())) {
            if (userDTO.getApproval().equals("o")) {
                user.setApproval("o");
            }
        }
        if (StringUtils.hasText(userDTO.getUserTel())) {
            user.setUserTel(userDTO.getUserTel());
        }
        Integer bus = userDTO.getUserBus();
        if (bus != null) {
            if (bus != 0) {
                user.setUserBus(userDTO.getUserBus());
            }
        }
        if (StringUtils.hasText(userDTO.getUserBirth())) {
            user.setUserBirth(userDTO.getUserBirth());
        }
        if (StringUtils.hasText(userDTO.getUserSchool())) {
            user.setUserSchool(userDTO.getUserSchool());
        }
        if (StringUtils.hasText(userDTO.getUserAddress())) {
            user.setUserAddress(userDTO.getUserAddress());
        }
        if (StringUtils.hasText(userDTO.getUserAddressDetail())) {
            user.setUserAddressDetail(userDTO.getUserAddressDetail());
        }
        if (StringUtils.hasText(userDTO.getUserConsultContent())) {
            user.setUserConsultContent(userDTO.getUserConsultContent());
        }
        if (StringUtils.hasText(userDTO.getUserSpecialNote())) {
            user.setUserSpecialNote(userDTO.getUserSpecialNote());
        }
        return userRepository.save(user);
    }

    @Override
    public User findById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }


    public User findByUserId(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found with userId: " + userId));
    }

    @Override
    public void modify(User modifyUser) {
        userRepository.save(modifyUser);
    }

    @Override
    public User login(String userId, String userPw) {
        Optional<User> loginUser = userRepository.findByUserId(userId);

        if(loginUser.isEmpty()) {
            throw new RuntimeException("id not exist");
        }

        if(!passwordEncoder.matches(userPw, loginUser.get().getUserPw())) {
            throw new RuntimeException("wrong pw");
        }

        return loginUser.get();
    }

    @Override
    public Page<User> getUserList(Pageable pageable, String searchCondition, String searchKeyword) {
        if(searchCondition.equals("all")) {
            if(searchKeyword.equals("")) {
                return userRepository.findAll(pageable);
            } else {
                return userRepository.findByUserNameContainingOrUserIdContainingOrUserTypeContainingOrUserTelContaining(searchKeyword, searchKeyword, searchKeyword, searchKeyword, pageable);
            }
        } else {
            if(searchKeyword.equals("")) {
                return userRepository.findAll(pageable);
            } else {
                if(searchCondition.equals("username")) {
                    return userRepository.findByUserNameContaining(searchKeyword, pageable);
                } else if(searchCondition.equals("useremail")) {
                    return userRepository.findByUserIdContaining(searchKeyword, pageable);
                } else if(searchCondition.equals("userno")) {
                    Integer usernoKeyword = Integer.parseInt(searchKeyword);
                    System.out.println(usernoKeyword);
                    System.out.println("이것이 회원넘버키워드");


                    return userRepository.findById(usernoKeyword, pageable);
                } else if(searchCondition.equals("usertype")) {
                    return userRepository.findByUserTypeContaining(searchKeyword, pageable);
                }else if(searchCondition.equals("userbus")) {
                    Integer busKeyword = Integer.parseInt(searchKeyword);
                    System.out.println(busKeyword);
                    System.out.println("이것이 버스키워드");
                    return userRepository.findByUserBus(busKeyword, pageable);
                }else if(searchCondition.equals("userTel")) {
                    return userRepository.findByUserTelContaining(searchKeyword, pageable);
                }



                else {
                    return userRepository.findAll(pageable);
                }
            }
        }
    }

    @Override
    public void deleteUser(int id) {
        User parent = findById(
                        findById(id)
                        .getUserJoinId());
        if (parent != null) {
            nchatService.nChatDelete(parent.EntityToDTO());
            userRepository.deleteById(parent.getId());
        }
        nchatService.nChatDelete(userRepository.findById(id).get().EntityToDTO());
        userRepository.deleteById(id);
    }

    //권한에 맞는 유저 리스트 뽑기
    @Override
    public List<User> getUserTypeList(String userType) {
        List<User> userList = userRepository.findAllByUserType(userType);
        return userList;
    }

    @Override
    public void increaseuserscore(Integer id) {
        userRepository.increaseuserscore(id);
    }

    @Override
    public long getUserCountCourse(int couNo) {
        return userRepository.countByUserTypeAndCourseCouNo("student", couNo);
    }

    @Override
    public List<User> getUserListForCouNo(int couNo) {
        return userRepository.findAllByCourseCouNo(couNo);
    }

    @Override
    public Page<User> getUserPage(int page, String category, String keyword) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("userRegdate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Specification<User> spec = searchByCategory(category, keyword);
        Page<User> userPageList = userRepository.findAll(spec, pageable);
        return userPageList;
    }

    @Override
    public Page<User> getUserTypePage(String type, int page, String category, String keyword) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("userRegdate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Specification<User> typeSpec = searchByUserType(type);
        Specification<User> searchSpec = searchByCategory(category, keyword);

        Specification<User> combinedSpec = typeSpec.and(searchSpec);

        Page<User> userPageList = userRepository.findAll(combinedSpec, pageable);
        return userPageList;
    }

    private Specification<User> searchByCategory(String category, String kw) {
        return (b, query, cb) -> {
            query.distinct(true);
            String likeKeyword = "%" + kw + "%";
            switch (category) {
                case "id":
                    return cb.like(b.get("userId"), likeKeyword);
                case "name":
                    return cb.like(b.get("userName"), likeKeyword);
                case "school":
                    return cb.like(b.get("userSchool"), likeKeyword);
                default:
                    return cb.or(
                            cb.like(b.get("userId"), likeKeyword),
                            cb.like(b.get("userName"), likeKeyword),
                            cb.like(b.get("userSchool"), likeKeyword)
                    );
            }
        };
    }

    private Specification<User> searchByUserType(String userType) {
        return (root, query, cb) -> {
            if (StringUtils.isEmpty(userType)) {
                return null; // userType이 비어 있으면 조건을 추가하지 않음
            }
            return cb.equal(root.get("userType"), userType);
        };
    }
}
