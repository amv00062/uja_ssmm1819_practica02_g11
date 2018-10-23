package data;

public class UserData {

    String userName="";
    String password="";
    String domain="";
    short port=0;

    /**
     * Constructor por defecto con los valores de labtelema.ujaen.es:80
     */
    public UserData(){
        userName="user";
        password="12345";
        domain="labtelema.ujaen.es";
        port=80;
    }

    /**
     * constructor con parámetros
     * @param user nombre de ususario
     * @param pass clave
     * @param domain dominio o ip del servidor
     * @param port puerto del servidor
     */
    public UserData(String user,String pass,String domain,short port){
        this.userName=user;
        this.password=password;
        this.domain=domain;
        this.port=port;
    }

    public String getUserName() {
        return userName;
    }

    public String getDomain() {
        return domain;
    }

    public short getPort() {
        return port;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setPort(short port) {
        this.port = port;
    }
}
