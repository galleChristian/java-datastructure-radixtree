package galle.yi.radixtrie.me;

/**
 * radix Trie implementation in Java
 * @author galle.yi
 * @link yiyikai@126.com
 * @link 824564329@qq.com
 * @see https://github.com/galleChristian/java-datastructure-radixtree
 * @apiNote only a-z UTF-8 for every public method in this class
 */
public class TestRemove {

	public static void main(String[] args) {

		String[] arr = { "tester", "slow", "water", "slower", 
				"test", "team", "toast", "today" };

		RedblackRadixTrieMap map = new RedblackRadixTrieMap();
		for (String s : arr) {
			map.put(s, s + " de meanling");
		}

		map.remove(arr[7]);
		map.remove(arr[6]);
		map.remove(arr[5]);
		map.remove(arr[4]);
		map.remove(arr[3]);
		System.out.println(map.size());

		System.out.println();
		for (String s : arr) {
			String info = map.get(s);
			System.out.println(info);
		}

	}
}
