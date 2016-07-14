package game.components;

import com.simsilica.es.EntityComponent;

/**
 * ¿‰»¥ ±º‰
 * @author yanmaoyuan
 *
 */
public class CoolDown implements EntityComponent {
    private long start;
    private long delta;

    public CoolDown( long deltaMillis ) {
        this.start = System.nanoTime();
        this.delta = deltaMillis * 1000000;
    }

    public double getPercent() {
        long time = System.nanoTime();
        return (double)(time - start)/delta;
    }

    @Override
    public String toString() {
        return "CoolDown[" + (delta/10000.0f) + "%]";
    }
}
