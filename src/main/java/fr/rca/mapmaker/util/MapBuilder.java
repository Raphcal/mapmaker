package fr.rca.mapmaker.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Constructeur de HashMap.
 *
 * @param <K> Type des clefs.
 * @param <V> Type des valeurs.
 * @author Raphaël Calabro <raph_kun at yahoo.fr>
 */
public class MapBuilder<K, V> {

    /**
     * Table en cours de construction.
     */
    private final HashMap<K, V> map;

    /**
     * Créé un builder avec une table vide.
     */
    public MapBuilder() {
        this.map = new HashMap<>();
    }

    /**
     * Créé un builder en copiant les données de la table donnée.
     *
     * @param other Table à copier.
     */
    public MapBuilder(Map<K, V> other) {
        this.map = new HashMap<>(other);
    }

    /**
     * Créé un nouveau MapBuilder et renvoie l'instance de
     * <code>LinkedHashMap</code> construite et initialisée grâce au
     * consommateur.
     *
     * @param <K> Type des clefs.
     * @param <V> Type des valeurs.
     * @param consumer Consommateur chargé d'initialiser la table.
     * @return L'instance de <code>LinkedHashMap</code> construite.
     */
    public static <K, V> Map<K,V> createMap(Consumer<MapBuilder<K, V>> consumer) {
        final MapBuilder<K, V> builder = new MapBuilder<>();
        consumer.accept(builder);
        return builder.build();
    }

    /**
     * Ajoute l'entrée donnée à la table.
     *
     * @param k Clef à ajouter.
     * @param v Valeur à associer à la clef donnée.
     * @return L'instance du constructeur.
     */
    public MapBuilder<K, V> put(K k, V v) {
        map.put(k, v);
        return this;
    }

    /**
     * Ajoute l'entrée donnée à la table seulement si la valeur n'est pas nulle.
     *
     * @param k Clef à ajouter.
     * @param v Valeur à associer à la clef donnée.
     * @return L'instance du constructeur.
     */
    public MapBuilder<K, V> putIfNotNull(K k, V v) {
        if (v != null) {
            map.put(k, v);
        }
        return this;
    }

    /**
     * Renvoi la table.
     *
     * @return La table.
     */
    public Map<K, V> build() {
        return map;
    }
    
}
