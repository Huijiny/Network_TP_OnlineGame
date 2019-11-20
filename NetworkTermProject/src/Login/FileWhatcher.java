package Login;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;

import DataStructure.User;
 
//import javax.annotation.PostConstruct;
 
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
 
//@RestController
public class FileWhatcher {
    //������Ʈ ���
   // private static final String projPath = System.getProperty("userinfo.txt");
    
    private WatchKey watchKey;
    
  //  @PostConstruct
    public void init() throws IOException {
        //watchService ����
        WatchService watchService = FileSystems.getDefault().newWatchService();
        //��� ����
        Path path = Paths.get("C:\\NW_TP\\NetworkTermProject");//���� ��Ʈ��ũ ������Ʈ ������ ����. �� �ȿ� userinfo.txt�־����.
        System.out.println(path.getFileName());
        System.out.println(path.getParent().getFileName());
        //�ش� ���丮 ��ο� ��ġ���񽺿� �̺�Ʈ ���
        path.register(watchService,
            StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_DELETE,
            StandardWatchEventKinds.ENTRY_MODIFY,
            StandardWatchEventKinds.OVERFLOW);
        
        Thread thread = new Thread(()-> {
            while(true) {
                try {
                    watchKey = watchService.take();//�̺�Ʈ�� ���� ���(Blocking)
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                List<WatchEvent<?>> events = watchKey.pollEvents();//�̺�Ʈ���� ������
                for(WatchEvent<?> event : events) {
                    //�̺�Ʈ ����
                    Kind<?> kind = event.kind();
                    //���
                    Path paths = (Path)event.context();
                    System.out.println(paths.toAbsolutePath());//C:\...\...\test.txt
                    if(kind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                        System.out.println("created something in directory");
                    }else if(kind.equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                        System.out.println("delete something in directory");
                    }else if(kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
                        System.out.println("modified something in directory");
                        test.getUserInfo();
                        
                    }else if(kind.equals(StandardWatchEventKinds.OVERFLOW)) {
                        System.out.println("overflow");
                    }else {
                        System.out.println("hello world");
                    }
                }
                if(!watchKey.reset()) {
                    try {
                        watchService.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }
    //@GetMapping("/")
    public String test() {
        System.out.println("userinfo.txt");        
        return "hello";
    }
	
}

