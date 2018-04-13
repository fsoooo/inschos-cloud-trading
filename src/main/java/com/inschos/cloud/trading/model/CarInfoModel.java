package com.inschos.cloud.trading.model;

import com.inschos.cloud.trading.assist.kit.CardCodeKit;
import com.inschos.cloud.trading.assist.kit.JsonKit;
import com.inschos.cloud.trading.assist.kit.StringKit;
import com.inschos.cloud.trading.extend.car.ExtendCarInsurancePolicy;

import java.util.Date;
import java.util.List;

/**
 * 创建日期：2018/3/26 on 11:59
 * 描述：
 * 作者：zhangyunhe
 */
public class CarInfoModel {

    /**
     * 主键
     */
    public String id;

    /**
     * 内部保单唯一标识
     */
    public String warranty_uuid;

    /**
     * 流水号
     */
    public String biz_id;

    /**
     * 第三方业务id
     */
    public String thp_biz_id;

    /**
     * 车险类型 1-强险，2-商业险
     */
    public String insurance_type;

    /**
     * 车牌号
     */
    public String car_code;

    /**
     * 车主姓名
     */
    public String name;

    /**
     * 车主证件号
     */
    public String card_code;

    /**
     * 车主证件类型
     */
    public String card_type;

    /**
     * 车主手机号
     */
    public String phone;

    /**
     * 生日
     */
    public String birthday;

    /**
     * 性别
     */
    public String sex;

    /**
     * 年龄
     */
    public String age;

    /**
     * 车架号
     */
    public String frame_no;

    /**
     * 发动机号
     */
    public String engine_no;

    /**
     * 发改委编码
     */
    public String vehicle_fgw_code;

    /**
     * 发改委名称
     */
    public String vehicle_fgw_name;

    /**
     * 品牌型号编码
     */
    public String brand_code;

    /**
     * 品牌名称
     */
    public String brand_name;

    /**
     * 年份款型
     */
    public String parent_veh_name;

    /**
     * 排量
     */
    public String engine_desc;

    /**
     * 投保时是否未上牌（0:否 1:是）
     */
    public String is_not_car_code;

    /**
     * 是否过户车（0:否 1:是）
     */
    public String is_trans;

    /**
     * 过户日期
     */
    public String trans_date;

    /**
     * 初登日期
     */
    public String first_register_date;

    /**
     * 车系名称
     */
    public String family_name;

    /**
     * 车挡类型
     */
    public String gearbox_type;

    /**
     * 备注
     */
    public String car_remark;

    /**
     * 新车购置价
     */
    public String new_car_price;

    /**
     * 含税价格
     */
    public String purchase_price_tax;

    /**
     * 进口标识，0：国产，1：合资，2：进口
     */
    public String import_flag;

    /**
     * 参考价
     */
    public String purchase_price;

    /**
     * 座位数
     */
    public String car_seat;

    /**
     * 款型名称
     */
    public String standard_name;

    /**
     * 险别列表json
     */
    public String coverage_list;

    /**
     * 创建时间
     */
    public String created_at;

    /**
     * 结束时间
     */
    public String updated_at;

    public CarInfoModel() {

    }

    public CarInfoModel(String warrantyUuid, String bizId, String thpBizId, String insuranceType, String time, String coverageList, ExtendCarInsurancePolicy.CarInfoDetail carInfoDetail, ExtendCarInsurancePolicy.InsuranceParticipant participant) {
        this.warranty_uuid = warrantyUuid;
        this.biz_id = bizId;
        this.thp_biz_id = thpBizId;
        this.insurance_type = insuranceType;
        this.car_code = carInfoDetail.licenseNo;
        this.name = participant.ownerName;
        this.card_code = participant.ownerID;
        this.card_type = participant.ownerIdType;
        this.phone = participant.ownerMobile;
        this.birthday = participant.ownerBirthday;
        this.sex = participant.ownerSex;
        if (StringKit.isInteger(participant.ownerIdType)) {
            Date birthDayByCode = CardCodeKit.getBirthDayByCode(Integer.valueOf(participant.ownerIdType), participant.ownerID);
            if (birthDayByCode == null) {
                this.age = participant.getAge(birthday);
            } else {
                this.age = participant.getAge(birthDayByCode);
            }
        } else {
            this.age = participant.getAge(birthday);
        }
        this.frame_no = carInfoDetail.frameNo;
        this.engine_no = carInfoDetail.engineNo;
        this.vehicle_fgw_code = carInfoDetail.vehicleFgwCode;
        this.vehicle_fgw_name = carInfoDetail.vehicleFgwName;
        this.brand_code = carInfoDetail.brandCode;
        this.is_not_car_code = StringKit.isEmpty(this.car_code) ? "1" : "0";
        this.is_trans = carInfoDetail.isTrans;
        this.brand_name = carInfoDetail.brandName;
        this.parent_veh_name = carInfoDetail.parentVehName;
        this.engine_desc = carInfoDetail.engineDesc;
        this.trans_date = carInfoDetail.transDateValue;
        this.first_register_date = carInfoDetail.firstRegisterDateValue;
        this.family_name = carInfoDetail.familyName;
        this.gearbox_type = carInfoDetail.gearboxType;
        this.car_remark = carInfoDetail.remark;
        this.new_car_price = carInfoDetail.newCarPrice;
        this.purchase_price_tax = carInfoDetail.purchasePriceTax;
        this.import_flag = carInfoDetail.importFlag;
        this.purchase_price = carInfoDetail.purchasePrice;
        this.car_seat = carInfoDetail.seat;
        this.standard_name = carInfoDetail.standardName;
        this.coverage_list = coverageList;
        this.created_at = time;
        this.updated_at = time;
    }


    // 强险
    public static final String INSURANCE_TYPE_STRONG = "1";
    // 商业险
    public static final String INSURANCE_TYPE_COMMERCIAL = "2";

    public String insuranceTypeText(String insuranceType) {
        String str = "";
        if (insuranceType == null) {
            return str;
        }
        // 车险类型 1-强险，2-商业险
        switch (insuranceType) {
            case INSURANCE_TYPE_STRONG:
                str = "强险";
                break;
            case INSURANCE_TYPE_COMMERCIAL:
                str = "商业险";
                break;
        }
        return str;
    }
    public String cardTypeText(String cardType) {
        return CardCodeKit.getCardTypeText(cardType);
    }

    // 国产
    public static final String CAR_DOMESTIC = "1";
    // 合资
    public static final String CAR_JOINT_VENTURE = "2";
    // 进口
    public static final String CAR_IMPORT = "3";

    public String importFlagText(String importFlag) {
        String str = "";
        if (importFlag == null) {
            return str;
        }
        // 进口标识，0：国产，1：合资，2：进口
        switch (importFlag) {
            case CAR_DOMESTIC:
                str = "国产";
                break;
            case CAR_JOINT_VENTURE:
                str = "合资";
                break;
            case CAR_IMPORT:
                str = "进口";
                break;
        }
        return str;
    }

    public List parseCoverageList (String coverageList) {
        return JsonKit.json2Bean(coverageList,List.class);
    }

}
