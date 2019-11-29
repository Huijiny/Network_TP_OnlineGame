package Login;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class MyObjectOutputStream extends ObjectOutputStream
{
 public MyObjectOutputStream(OutputStream out) throws IOException
 {
  // TODO Auto-generated constructor stub
  super(out);
 }
 
 @Override
 protected void writeStreamHeader() throws IOException
 {
  // TODO Auto-generated method stub
  //헤더저장할때 아무것도 않하기
	 reset();
 }
}
