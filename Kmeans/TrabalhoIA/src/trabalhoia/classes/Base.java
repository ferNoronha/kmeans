package trabalhoia.classes;

/**
 *
 * @author Aluno
 */
public class Base {

    float v[];
    int id, classe;
    String original;

    public Base(float[] v, int classe, int id, String original) {
        this.v = v;
        this.classe = classe;
        this.id = id;
        this.original = original;
    }

    public float[] getV() {
        return v;
    }

    public void setV(float[] v) {
        this.v = v;
    }

    public int getClasse() {
        return classe;
    }

    public void setClasse(int classe) {
        this.classe = classe;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

}
