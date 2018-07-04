package com.liaoliao.chat.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.liaoliao.chat.model.request.AddGroupMemberRequest;
import com.liaoliao.chat.model.request.AddToBlackListRequest;
import com.liaoliao.chat.model.request.AgreeFriendsRequest;
import com.liaoliao.chat.model.request.ChangePasswordRequest;
import com.liaoliao.chat.model.request.CheckPhoneRequest;
import com.liaoliao.chat.model.request.CreateGroupRequest;
import com.liaoliao.chat.model.request.DeleteFriendRequest;
import com.liaoliao.chat.model.request.DeleteGroupMemberRequest;
import com.liaoliao.chat.model.request.DismissGroupRequest;
import com.liaoliao.chat.model.request.FriendInvitationRequest;
import com.liaoliao.chat.model.request.JoinGroupRequest;
import com.liaoliao.chat.model.request.LoginRequest;
import com.liaoliao.chat.model.request.QuitGroupRequest;
import com.liaoliao.chat.model.request.RegisterRequest;
import com.liaoliao.chat.model.request.RemoveFromBlacklistRequest;
import com.liaoliao.chat.model.request.RestPasswordRequest;
import com.liaoliao.chat.model.request.SendCodeRequest;
import com.liaoliao.chat.model.request.SetFriendDisplayNameRequest;
import com.liaoliao.chat.model.request.SetGroupDisplayNameRequest;
import com.liaoliao.chat.model.request.SetGroupNameRequest;
import com.liaoliao.chat.model.request.SetGroupPortraitRequest;
import com.liaoliao.chat.model.request.SetNameRequest;
import com.liaoliao.chat.model.request.SetPortraitRequest;
import com.liaoliao.chat.model.request.VerifyCodeRequest;
import com.liaoliao.chat.model.response.AddGroupMemberResponse;
import com.liaoliao.chat.model.response.AddToBlackListResponse;
import com.liaoliao.chat.model.response.AgreeFriendsResponse;
import com.liaoliao.chat.model.response.ChangePasswordResponse;
import com.liaoliao.chat.model.response.CheckPhoneResponse;
import com.liaoliao.chat.model.response.CreateGroupResponse;
import com.liaoliao.chat.model.response.DefaultConversationResponse;
import com.liaoliao.chat.model.response.DeleteFriendResponse;
import com.liaoliao.chat.model.response.DeleteGroupMemberResponse;
import com.liaoliao.chat.model.response.FriendInvitationResponse;
import com.liaoliao.chat.model.response.GetBlackListResponse;
import com.liaoliao.chat.model.response.GetFriendInfoByIDResponse;
import com.liaoliao.chat.model.response.GetGroupInfoResponse;
import com.liaoliao.chat.model.response.GetGroupMemberResponse;
import com.liaoliao.chat.model.response.GetGroupResponse;
import com.liaoliao.chat.model.response.GetTokenResponse;
import com.liaoliao.chat.model.response.GetUserInfoByIdResponse;
import com.liaoliao.chat.model.response.GetUserInfoByPhoneResponse;
import com.liaoliao.chat.model.response.GetUserInfosResponse;
import com.liaoliao.chat.model.response.JoinGroupResponse;
import com.liaoliao.chat.model.response.LoginResponse;
import com.liaoliao.chat.model.response.QiNiuTokenResponse;
import com.liaoliao.chat.model.response.QuitGroupResponse;
import com.liaoliao.chat.model.response.RegisterResponse;
import com.liaoliao.chat.model.response.RemoveFromBlackListResponse;
import com.liaoliao.chat.model.response.RestPasswordResponse;
import com.liaoliao.chat.model.response.SendCodeResponse;
import com.liaoliao.chat.model.response.SetFriendDisplayNameResponse;
import com.liaoliao.chat.model.response.SetGroupDisplayNameResponse;
import com.liaoliao.chat.model.response.SetGroupNameResponse;
import com.liaoliao.chat.model.response.SetGroupPortraitResponse;
import com.liaoliao.chat.model.response.SetNameResponse;
import com.liaoliao.chat.model.response.SetPortraitResponse;
import com.liaoliao.chat.model.response.SyncTotalDataResponse;
import com.liaoliao.chat.model.response.UserRelationshipResponse;
import com.liaoliao.chat.model.response.VerifyCodeResponse;
import com.liaoliao.chat.model.response.VersionResponse;


import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * @创建者 CSDN_LQR
 * @描述 使用Retrofit对网络请求进行配置
 */
public class ApiRetrofit extends BaseApiRetrofit {

    public MyApi mApi;
    public static ApiRetrofit mInstance;

    private ApiRetrofit() {
        super();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        //在构造方法中完成对Retrofit接口的初始化
        mApi = new Retrofit.Builder()
                .baseUrl(MyApi.BASE_URL)
                .client(getClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(MyApi.class);
    }

    public static ApiRetrofit getInstance() {
        if (mInstance == null) {
            synchronized (ApiRetrofit.class) {
                if (mInstance == null) {
                    mInstance = new ApiRetrofit();
                }
            }
        }
        return mInstance;
    }

    private RequestBody getRequestBody(Object obj) {
        String route = new Gson().toJson(obj);
        RequestBody body= RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),route);
//        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), route);
        return body;
    }

    //登录
    public Observable<LoginResponse> login(String region, String phone, String password) {
        return mApi.login(getRequestBody(new LoginRequest(region, phone, password)));
    }

    //注册
    public Observable<CheckPhoneResponse> checkPhoneAvailable(String region, String phone) {
        return mApi.checkPhoneAvailable(getRequestBody(new CheckPhoneRequest(phone, region)));
    }

    public Observable<SendCodeResponse> sendCode(String region, String phone) {
        return mApi.sendCode(getRequestBody(new SendCodeRequest(region, phone)));
    }

    public Observable<VerifyCodeResponse> verifyCode(String region, String phone, String code) {
        return mApi.verifyCode(getRequestBody(new VerifyCodeRequest(region, phone, code)));
    }

    public Observable<RegisterResponse> register(String nickname, String password, String verification_token) {
        return mApi.register(getRequestBody(new RegisterRequest(nickname, password, verification_token)));
    }

    public Observable<GetTokenResponse> getToken() {
        return mApi.getToken();
    }

    //个人信息
    public Observable<SetNameResponse> setName(String nickName) {
        return mApi.setName(getRequestBody(new SetNameRequest(nickName)));
    }

    public Observable<SetPortraitResponse> setPortrait(String portraitUri) {
        return mApi.setPortrait(getRequestBody(new SetPortraitRequest(portraitUri)));
    }

    public Observable<ChangePasswordResponse> changePassword(String oldPassword, String newPassword) {
        return mApi.changePassword(getRequestBody(new ChangePasswordRequest(oldPassword, newPassword)));
    }

    /**
     * @param password           密码，6 到 20 个字节，不能包含空格
     * @param verification_token 调用 /user/verify_code 成功后返回的 activation_token
     */
    public Observable<RestPasswordResponse> restPassword(String password, String verification_token) {
        return mApi.restPassword(getRequestBody(new RestPasswordRequest(password, verification_token)));
    }

    //查询
    public Observable<GetUserInfoByIdResponse> getUserInfoById(String userid) {
        return mApi.getUserInfoById(userid);
    }

    public Observable<GetUserInfoByPhoneResponse> getUserInfoFromPhone(String region, String phone) {
        return mApi.getUserInfoFromPhone(region, phone);
    }

    //好友
    public Observable<FriendInvitationResponse> sendFriendInvitation(String userid, String addFriendMessage) {
        return mApi.sendFriendInvitation(getRequestBody(new FriendInvitationRequest(userid, addFriendMessage)));
    }

    public Observable<UserRelationshipResponse> getAllUserRelationship() {
        return mApi.getAllUserRelationship();
    }

    public Observable<GetFriendInfoByIDResponse> getFriendInfoByID(String userid) {
        return mApi.getFriendInfoByID(userid);
    }

    public Observable<AgreeFriendsResponse> agreeFriends(String friendId) {
        return mApi.agreeFriends(getRequestBody(new AgreeFriendsRequest(friendId)));
    }

    public Observable<DeleteFriendResponse> deleteFriend(String friendId) {
        return mApi.deleteFriend(getRequestBody(new DeleteFriendRequest(friendId)));
    }

    public Observable<SetFriendDisplayNameResponse> setFriendDisplayName(String friendId, String displayName) {
        return mApi.setFriendDisplayName(getRequestBody(new SetFriendDisplayNameRequest(friendId, displayName)));
    }

    public Observable<GetBlackListResponse> getBlackList() {
        return mApi.getBlackList();
    }

    public Observable<AddToBlackListResponse> addToBlackList(String friendId) {
        return mApi.addToBlackList(getRequestBody(new AddToBlackListRequest(friendId)));
    }

    public Observable<RemoveFromBlackListResponse> removeFromBlackList(String friendId) {
        return mApi.removeFromBlackList(getRequestBody(new RemoveFromBlacklistRequest(friendId)));
    }


    //群组
    public Observable<CreateGroupResponse> createGroup(String name, List<String> memberIds) {
        return mApi.createGroup(getRequestBody(new CreateGroupRequest(name, memberIds)));
    }

    public Observable<SetGroupPortraitResponse> setGroupPortrait(String groupId, String portraitUri) {
        return mApi.setGroupPortrait(getRequestBody(new SetGroupPortraitRequest(groupId, portraitUri)));
    }

    public Observable<GetGroupResponse> getGroups() {
        return mApi.getGroups();
    }

    public Observable<GetGroupInfoResponse> getGroupInfo(String groupId) {
        return mApi.getGroupInfo(groupId);
    }

    public Observable<GetGroupMemberResponse> getGroupMember(String groupId) {
        return mApi.getGroupMember(groupId);
    }

    public Observable<AddGroupMemberResponse> addGroupMember(String groupId, List<String> memberIds) {
        return mApi.addGroupMember(getRequestBody(new AddGroupMemberRequest(groupId, memberIds)));
    }

    public Observable<DeleteGroupMemberResponse> deleGroupMember(String groupId, List<String> memberIds) {
        return mApi.deleGroupMember(getRequestBody(new DeleteGroupMemberRequest(groupId, memberIds)));
    }

    public Observable<SetGroupNameResponse> setGroupName(String groupId, String name) {
        return mApi.setGroupName(getRequestBody(new SetGroupNameRequest(groupId, name)));
    }

    public Observable<QuitGroupResponse> quitGroup(String groupId) {
        return mApi.quitGroup(getRequestBody(new QuitGroupRequest(groupId)));
    }

    public Observable<QuitGroupResponse> dissmissGroup(String groupId) {
        return mApi.dissmissGroup(getRequestBody(new DismissGroupRequest(groupId)));
    }
//    public Observable<DismissGroupResponse> dissmissGroup(String groupId) {
//        return mApi.dissmissGroup(getRequestBody(new DismissGroupRequest(groupId)));
//    }

    public Observable<SetGroupDisplayNameResponse> setGroupDisplayName(String groupId, String displayName) {
        return mApi.setGroupDisplayName(getRequestBody(new SetGroupDisplayNameRequest(groupId, displayName)));
    }

    public Observable<JoinGroupResponse> JoinGroup(String groupId) {
        return mApi.JoinGroup(getRequestBody(new JoinGroupRequest(groupId)));
    }

    public Observable<DefaultConversationResponse> getDefaultConversation() {
        return mApi.getDefaultConversation();
    }

    public Observable<GetUserInfosResponse> getUserInfos(List<String> ids) {
        StringBuilder sb = new StringBuilder();
        for (String s : ids) {
            sb.append("id=");
            sb.append(s);
            sb.append("&");
        }
        String stringRequest = sb.substring(0, sb.length() - 1);
        return mApi.getUserInfos(stringRequest);
    }

    //其他
    public Observable<QiNiuTokenResponse> getQiNiuToken() {
        return mApi.getQiNiuToken();
    }

    public Observable<VersionResponse> getClientVersion() {
        return mApi.getClientVersion();
    }

    public Observable<SyncTotalDataResponse> syncTotalData(String version) {
        return mApi.syncTotalData(version);
    }

}
