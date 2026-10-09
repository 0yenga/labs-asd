// Односвязный список строк, реализован вручную на узлах.
// Готовые коллекции JDK здесь не используются.
public class ListNode {
    // Информационное поле узла.
    public String data;
    // Ссылка на следующий узел списка.
    public ListNode next;

    // Создание узла с заданным значением.
    public ListNode(String value) {
        this.data = value;
        this.next = null;
    }
}
