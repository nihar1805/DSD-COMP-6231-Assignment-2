public class ICallback_Impl implements ICallback{

    private String val;
    public ICallback_Impl(String val) {
        this.val = val;
    }

    @Override
    public String getValue() {
        return val;
    }
}
