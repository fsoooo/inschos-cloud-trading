package com.inschos.cloud.trading.access.http.controller.action;


import com.inschos.cloud.trading.access.http.controller.bean.ActionBean;
import com.inschos.cloud.trading.access.http.controller.bean.BankBean;
import com.inschos.cloud.trading.access.http.controller.bean.BaseResponse;
import com.inschos.cloud.trading.access.rpc.client.PayAuthServiceClient;
import com.inschos.cloud.trading.annotation.CheckParamsKit;
import com.inschos.cloud.trading.data.dao.BankAuthorizeDao;
import com.inschos.cloud.trading.data.dao.BankDao;
import com.inschos.cloud.trading.model.Bank;
import com.inschos.cloud.trading.model.BankAuthorize;
import com.inschos.common.assist.kit.StringKit;
import com.inschos.common.assist.kit.TimeKit;
import com.inschos.dock.bean.BankConfirmBean;
import com.inschos.dock.bean.RpcResponse;
import com.inschos.dock.bean.RspBankApplyBean;
import com.inschos.dock.bean.RspBankConfirmBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * author   meiming_mm@163.com
 * date     2018/7/11
 * version  v1.0.0
 */
@Component
public class BankAction extends BaseAction {

    @Autowired
    private BankDao bankDao;
    @Autowired
    private BankAuthorizeDao bankAuthorizeDao;

    @Autowired
    private PayAuthServiceClient payAuthServiceClient;

    public String add(ActionBean bean) {

        BankBean.UpdateRequest request = requst2Bean(bean.body, BankBean.UpdateRequest.class);
        BaseResponse response = new BaseResponse();

        List<String> ignores = new ArrayList<>();
        ignores.add("id");
        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request, ignores);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }


        Bank bank = new Bank();
        bank.account_uuid = bean.accountUuid;
        bank.bank_code = request.bankCode;

        if (bankDao.findExistsOne(bank) == null) {

            bank.bank_name = request.bankName;
            bank.bank_city = request.bankCity;
            bank.phone = request.bankPhone;
            bank.status = Bank.BANK_AUTH_STATUS_NO;
            bank.state = 1;
            bank.created_at = bank.updated_at = TimeKit.currentTimeMillis();
            if (bankDao.add(bank) > 0) {
                return json(BaseResponse.CODE_SUCCESS, "添加成功", response);
            } else {
                return json(BaseResponse.CODE_FAILURE, "添加失败", response);
            }
        } else {
            return json(BaseResponse.CODE_FAILURE, "该银行卡已存在", response);
        }

    }

    public String modify(ActionBean bean) {

        BankBean.UpdateRequest request = requst2Bean(bean.body, BankBean.UpdateRequest.class);
        BaseResponse response = new BaseResponse();

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        long bankId = Long.valueOf(request.id);

        Bank bank = bankDao.findOne(bankId);

        if (bank == null) {
            return json(BaseResponse.CODE_FAILURE, "更新失败", response);
        } else if (bank.status == Bank.BANK_AUTH_STATUS_OK) {
            return json(BaseResponse.CODE_FAILURE, "此卡已授权，无法执行更新", response);
        }

        //判断是否修改卡号
        if (!bank.bank_code.equals(request.bankCode)) {
            //验证需更改的卡号是否已存在
            Bank search = new Bank();
            search.account_uuid = bean.accountUuid;
            search.bank_code = request.bankCode;
            if (bankDao.findExistsOne(bank) != null) {
                return json(BaseResponse.CODE_FAILURE, "该银行卡已存在", response);
            }
        }

        bank.bank_name = request.bankName;
        bank.bank_city = request.bankCity;
        bank.phone = request.bankPhone;
        bank.status = Bank.BANK_AUTH_STATUS_NO;
        bank.updated_at = TimeKit.currentTimeMillis();

        if (bankDao.update(bank) > 0) {
            return json(BaseResponse.CODE_SUCCESS, "修改成功", response);
        } else {
            return json(BaseResponse.CODE_FAILURE, "修改失败", response);
        }
    }

    public String list(ActionBean bean) {

        BankBean.ListRequest request = requst2Bean(bean.body, BankBean.ListRequest.class);
        BaseResponse response = new BaseResponse();

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }
        Bank search = new Bank();
        search.account_uuid = bean.accountUuid;
        List<Bank> bankList = bankDao.findListByAuuid(search);

        List<BankBean.BankData> dataList = new ArrayList<>();

        if (bankList != null && !bankList.isEmpty()) {
            BankAuthorize bankAuthorize = bankAuthorizeDao.findByAUuid(bean.accountUuid);
            long bindId = 0;
            if (bankAuthorize != null) {
                bindId = bankAuthorize.bank_id;
            }
            for (Bank bank : bankList) {
                BankBean.BankData data = toData(bank);
                if (data.id == bindId) {
                    data.isUsing = 1;
                }
                dataList.add(data);
            }
        }
        response.data = dataList;
        return json(BaseResponse.CODE_SUCCESS, "成功", response);
    }

    public String detail(ActionBean bean) {

        BankBean.DetailRequest request = requst2Bean(bean.body, BankBean.DetailRequest.class);
        BaseResponse response = new BaseResponse();

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        Bank bank = bankDao.findOne(Long.valueOf(request.id));
        if (bank != null) {
            BankBean.BankData data = toData(bank);
            response.data = data;
            return json(BaseResponse.CODE_SUCCESS, "成功", response);
        } else {
            return json(BaseResponse.CODE_FAILURE, "获取详情失败", response);
        }
    }


    public String remove(ActionBean bean) {

        BankBean.DetailRequest request = requst2Bean(bean.body, BankBean.DetailRequest.class);
        BaseResponse response = new BaseResponse();

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }
        long bankId = Long.valueOf(request.id);
        Bank bank = bankDao.findOne(bankId);

        if (bank != null) {

            if (bank.status == Bank.BANK_AUTH_STATUS_OK) {
                // TODO: 2018/7/12 银行解绑
                BankAuthorize bankAuthorize = bankAuthorizeDao.findByAUuid(bean.accountUuid);
                if (bankAuthorize != null && bankAuthorize.bank_id == bankId) {
                    bankAuthorize.bank_id = 0;
                    bankAuthorize.updated_at = TimeKit.currentTimeMillis();
                    bankAuthorizeDao.update(bankAuthorize);
                }
            }
            bank.state = 0;
            bank.updated_at = TimeKit.currentTimeMillis();

            if (bankDao.updateState(bank) > 0) {
                return json(BaseResponse.CODE_SUCCESS, "删除成功", response);
            } else {
                return json(BaseResponse.CODE_FAILURE, "删除失败", response);
            }
        } else {
            return json(BaseResponse.CODE_FAILURE, "删除失败", response);
        }
    }

    public String applyAuth(ActionBean bean) {
        BankBean.ApplyAuthRequest request = requst2Bean(bean.body, BankBean.ApplyAuthRequest.class);
        BankBean.ApplyAuthResponse response = new BankBean.ApplyAuthResponse();

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        //鉴权发送
        com.inschos.dock.bean.BankBean bankBean = new com.inschos.dock.bean.BankBean();
        bankBean.userName = request.name;
        bankBean.bankCode = request.bankCode;
        bankBean.phone = request.bankPhone;
        bankBean.idCard = request.idCard;
        RpcResponse<RspBankApplyBean> rpcResponse = payAuthServiceClient.bankApplyAuth(request.origin, bankBean);

        if (rpcResponse != null) {
            if (rpcResponse.code == RpcResponse.CODE_SUCCESS) {
                return json(BaseResponse.CODE_SUCCESS, rpcResponse.message, response);
            } else {
                return json(BaseResponse.CODE_FAILURE, StringKit.isEmpty(rpcResponse.esbErrCode) ? rpcResponse.message : rpcResponse.esbErrCode, response);
            }
        } else {
            return json(BaseResponse.CODE_FAILURE, "发送失败", response);
        }
    }

    public String confirmAuth(ActionBean bean) {
        BankBean.ConfirmAuthRequest request = requst2Bean(bean.body, BankBean.ConfirmAuthRequest.class);
        BaseResponse response = new BaseResponse();

        List<CheckParamsKit.Entry<String, String>> entries = checkParams(request);
        if (entries != null) {
            return json(BaseResponse.CODE_PARAM_ERROR, entries, response);
        }

        //  鉴权确认
        BankConfirmBean bankConfirmBean = new BankConfirmBean();
        bankConfirmBean.requestId = request.requestId;
        bankConfirmBean.vdCode = request.vdCode;
        bankConfirmBean.userName = request.name;
        bankConfirmBean.bankCode = request.bankCode;
        bankConfirmBean.phone = request.bankPhone;
        bankConfirmBean.idCard = request.idCard;
        RpcResponse<RspBankConfirmBean> rpcResponse = payAuthServiceClient.bankConfirmAuth(request.origin, bankConfirmBean);

        boolean authOk = rpcResponse != null && rpcResponse.code == RpcResponse.CODE_SUCCESS;
        int exResult = 0;
        if (authOk) {

            Bank bank = new Bank();
            bank.account_uuid = bean.accountUuid;
            bank.bank_code = request.bankCode;
            bank.phone = request.bankPhone;
            bank.status = Bank.BANK_AUTH_STATUS_OK;

            exResult = bankDao.applyAuth(bank);
        }


        if (exResult > 0) {
            return json(BaseResponse.CODE_SUCCESS, rpcResponse.message, response);
        } else {
            String errMsg = rpcResponse != null ?
                    (rpcResponse.code == BaseResponse.CODE_SUCCESS || StringKit.isEmpty(rpcResponse.esbErrCode) ?
                            "授权失败" : rpcResponse.esbErrCode)
                    : "授权失败";
            return json(BaseResponse.CODE_FAILURE, errMsg, response);
        }

    }

    public String getUsedPayInfo(ActionBean bean) {

        BankBean.GetUsePayInfoResponse response = new BankBean.GetUsePayInfoResponse();


        BankAuthorize bankAuthorize = bankAuthorizeDao.findByAUuid(bean.accountUuid);

        if (bankAuthorize != null) {

            if (bankAuthorize.bank_id > 0) {
                Bank bank = bankDao.findOne(bankAuthorize.bank_id);
                if (bank != null) {
                    response.bank = toData(bank);
                }
            }

            if (!StringKit.isEmpty(bankAuthorize.openid)) {
                response.weixin = new BankBean.WeiXinData();
                response.weixin.openid = bankAuthorize.openid;
                response.weixin.changeType = bankAuthorize.change_type;
                response.weixin.contractCode = bankAuthorize.contract_code;
                response.weixin.contractExpiredTime = bankAuthorize.contract_expired_time;
                response.weixin.contractId = bankAuthorize.contract_id;
                response.weixin.requestSerial = bankAuthorize.request_serial;
            }
        }
        return json(BaseResponse.CODE_SUCCESS,"获取成功",response);

    }

    private BankBean.BankData toData(Bank bank) {
        if (bank != null) {
            BankBean.BankData data = new BankBean.BankData();
            data.id = bank.id;
            data.bankName = bank.bank_name;
            data.bankCity = bank.bank_city;
            data.bankCode = bank.bank_code;
            data.bankType = bank.bank_type;
            data.bankPhone = bank.phone;
            data.status = bank.status;
            return data;
        } else {
            return null;
        }

    }
}
