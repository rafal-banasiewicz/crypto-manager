package pl.rb.manager.model.zonda;

public class ZondaItem {

    String id;
    String market;
    String time;
    String amount;
    String rate;
    String initializedBy;
    String wasTaker;
    String userAction;
    String offerId;
    String commissionValue;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getInitializedBy() {
        return initializedBy;
    }

    public void setInitializedBy(String initializedBy) {
        this.initializedBy = initializedBy;
    }

    public String getWasTaker() {
        return wasTaker;
    }

    public void setWasTaker(String wasTaker) {
        this.wasTaker = wasTaker;
    }

    public String getUserAction() {
        return userAction;
    }

    public void setUserAction(String userAction) {
        this.userAction = userAction;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getCommissionValue() {
        return commissionValue;
    }

    public void setCommissionValue(String commissionValue) {
        this.commissionValue = commissionValue;
    }

    @Override
    public String toString() {
        return "ZondaItem{" +
                "id='" + id + '\'' +
                ", market='" + market + '\'' +
                ", time='" + time + '\'' +
                ", amount='" + amount + '\'' +
                ", rate='" + rate + '\'' +
                ", initializedBy='" + initializedBy + '\'' +
                ", wasTaker='" + wasTaker + '\'' +
                ", userAction='" + userAction + '\'' +
                ", offerId='" + offerId + '\'' +
                ", commissionValue='" + commissionValue + '\'' +
                '}';
    }
}
