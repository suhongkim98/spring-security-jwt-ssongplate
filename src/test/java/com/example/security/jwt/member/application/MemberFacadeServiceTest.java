package com.example.security.jwt.member.application;

import com.example.security.jwt.account.domain.AccountDomainService;
import com.example.security.jwt.account.domain.exception.AccountConflictDomainException;
import com.example.security.jwt.global.exception.ApplicationException;
import com.example.security.jwt.member.application.dto.RegisterMemberFacadeRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class MemberFacadeServiceTest {

    @Mock
    private AccountDomainService accountDomainService;

    @InjectMocks
    private MemberFacadeServiceImpl memberFacadeService;

    @Test
    @DisplayName("회원가입 - 중복되지 않은 username이면 계정을 생성한다")
    void signup_whenUsernameNotDuplicated_createsAccount() {
        // given
        RegisterMemberFacadeRequestDto request = RegisterMemberFacadeRequestDto.builder()
                .username("newmember")
                .password("password123")
                .nickname("새회원")
                .build();
        doNothing().when(accountDomainService)
                .createUserAccount("newmember", "password123", "새회원");

        // when & then
        assertThatCode(() -> memberFacadeService.signup(request))
                .doesNotThrowAnyException();
        then(accountDomainService).should().createUserAccount("newmember", "password123", "새회원");
    }

    @Test
    @DisplayName("회원가입 - username이 중복이면 CONFLICT_MEMBER_ACCOUNT 예외를 던진다")
    void signup_whenUsernameDuplicated_throwsApplicationException() {
        // given
        RegisterMemberFacadeRequestDto request = RegisterMemberFacadeRequestDto.builder()
                .username("existing")
                .password("password123")
                .nickname("기존회원")
                .build();
        doThrow(new AccountConflictDomainException())
                .when(accountDomainService)
                .createUserAccount("existing", "password123", "기존회원");

        // when & then
        assertThatThrownBy(() -> memberFacadeService.signup(request))
                .isInstanceOf(ApplicationException.class)
                .satisfies(e -> assertThat(((ApplicationException) e).getErrorCode())
                        .isEqualTo(MemberErrorCode.CONFLICT_MEMBER_ACCOUNT));
    }
}
