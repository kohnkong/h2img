package testh2img;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import chrriis.dj.nativeswing.swtimpl.NativeComponent;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserEvent;

public class ScreenshotUtils extends JPanel {  
   
    /**
	 * 
	 */
	private static final long serialVersionUID = -5093331131951249940L;
	// 行分隔符  
    final static public String LS = System.getProperty("line.separator", "\n");
    // 文件分割符  
    final static public String FS = System.getProperty("file.separator", "\\");
    //以javascript脚本获得网页全屏后大小  
    final static StringBuffer jsDimension;
      
    static {
        jsDimension = new StringBuffer();
        jsDimension.append("var width = 0;").append(LS);
        jsDimension.append("var height = 0;").append(LS);
        jsDimension.append("if(document.documentElement) {").append(LS);
        jsDimension.append(
                        "  width = Math.max(width, document.documentElement.scrollWidth);")
                        //"  width = Math.max(width, document.documentElement.clientWidth);")
                        //"  width = document.documentElement.scrollWidth;")
                .append(LS);
        jsDimension.append(
                        "  height = Math.max(height, document.documentElement.scrollHeight);")
                        //"  height = Math.max(height, document.documentElement.clientHeight);")
                        //"  height = document.documentElement.scrollHeight;")
                .append(LS);
        jsDimension.append("}").append(LS);
        jsDimension.append("if(self.innerWidth) {").append(LS);
        jsDimension.append("  width = Math.max(width, self.innerWidth);")
                .append(LS);  
        jsDimension.append("  height = Math.max(height, self.innerHeight);")
                .append(LS);  
        jsDimension.append("}").append(LS);  
        jsDimension.append("if(document.body.scrollWidth) {").append(LS);  
        jsDimension.append(  
                "  width = Math.max(width, document.body.scrollWidth);")
                //"  width = Math.max(width, document.body.clientWidth);")
                //"  width = document.body.scrollWidth;")
                .append(LS);  
        jsDimension.append(
                "  height = Math.max(height, document.body.scrollHeight);")
                //"  height = Math.max(height, document.body.clientHeight);")
                //"  height = document.body.scrollHeight;") 
                .append(LS);  
        jsDimension.append("}").append(LS);  
        jsDimension.append("return width + ':' + height;");  
    }  

    public ScreenshotUtils(final String url, final int maxWidth, final int maxHeight) {
        super(new BorderLayout());
        JPanel webBrowserPanel = new JPanel(new BorderLayout());
        final String fileNamePrefix = "H:" + FS + "temp" + FS + "html2images" + FS + "10616";
        final String pngPostfix = ".png";
        final String fileName = "H:" + FS + "temp" + FS + "html2images" + FS + System.currentTimeMillis() + ".png";
        final JWebBrowser webBrowser = new JWebBrowser(null);
        webBrowser.setBarsVisible(false);
        webBrowser.navigate(url);
        webBrowserPanel.add(webBrowser, BorderLayout.CENTER);
        add(webBrowserPanel, BorderLayout.CENTER);
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
        webBrowser.addWebBrowserListener(new WebBrowserAdapter() {
            // 监听加载进度  
            public void loadingProgressChanged(WebBrowserEvent e) {
                // 当加载完毕时  
                if (e.getWebBrowser().getLoadingProgress() == 100) {
                    String result = (String) webBrowser
                            .executeJavascriptWithResult(jsDimension.toString());
                    int index = result == null ? -1 : result.indexOf(":");
                    NativeComponent nativeComponent = webBrowser
                            .getNativeComponent();
                    Dimension originalSize = nativeComponent.getSize();
                    Dimension imageSize = new Dimension(Integer.parseInt(result
                            .substring(0, index)), Integer.parseInt(result
                            .substring(index + 1)));
                    imageSize.width = Math.max(originalSize.width,
                            imageSize.width + 50);
                    imageSize.height = Math.max(originalSize.height,
                            imageSize.height + 50);
                    //更改尺寸
                    imageSize.height = imageSize.height*imageSize.width/1280;
                    imageSize.width = 1280;
                    
                    nativeComponent.setSize(imageSize);
                    BufferedImage image = new BufferedImage(imageSize.width,
                            imageSize.height, BufferedImage.TYPE_INT_RGB);
                    nativeComponent.paintComponent(image);
                    nativeComponent.setSize(originalSize);
                    System.out.println("imageSize.width : " + imageSize.width);
                    System.out.println("imageSize.height : " + imageSize.height);
                    // 当网页超出目标大小时
                    if (imageSize.width > maxWidth || imageSize.height > maxHeight) {
                        //截图部分图形  
                        //image = image.getSubimage(0, 0, maxWidth, maxHeight);  
                        //此部分为使用缩略图 
                        /*int width = image.getWidth(), height = image 
                            .getHeight(); 
                         AffineTransform tx = new AffineTransform(); 
                        tx.scale((double) maxWidth / width, (double) maxHeight 
                                / height); 
                        AffineTransformOp op = new AffineTransformOp(tx, 
                                AffineTransformOp.TYPE_NEAREST_NEIGHBOR); 
                        //缩小 
                        image = op.filter(image, null);*/
                    }
                    try {
                        // 输出图像  
                        //ImageIO.write(image, "png", new File(fileName));
                        
                        //输出多张图片
                        int perHight = 300;
                        for(int startHight = 0 ; startHight < imageSize.height ; startHight+=perHight){
                        	BufferedImage newImage;
                        	if(startHight + perHight <= imageSize.height ){
                        		newImage = image.getSubimage(0, startHight, imageSize.width-20, perHight);//去滚动条水平-20
                        	}else{
                        		newImage = image.getSubimage(0, startHight, imageSize.width-20, imageSize.height-startHight);
                        	}
                        	
                        	//判断是否纯色
                        	int colorWidth = newImage.getWidth()-20;
                        	int colorHeight = newImage.getHeight();
                        	int pixel = 0;//像素值
                        	int pixelTemp = 0;//对比像素值
                        	int pixelCount = 0;//相同像素计数
                        	boolean notPure = true;//纯色标志，纯色不保存
                        	for(int i = 1 ; i<colorWidth ; i++){
                        		for(int j = 1 ; j<colorHeight ; j++){
                        			pixel = newImage.getRGB(i, j);
                        			if(pixel==pixelTemp){
                        				pixelCount++;
                        			}else{
                        				pixelCount=0;
                        			}
                        			pixelTemp = pixel;
                        		}
                        	}
                        	System.out.println(pixelCount);
                        	System.out.println(colorWidth*colorHeight);
                        	//如果连续相同的像素点大于设定的百分比的话，就判定为是纯色的图片，此处设定全相同
                			if((float)pixelCount/(colorWidth*colorHeight)>=0.95){
                				notPure=false;
                				System.out.println("纯色");
                			}else{
                				System.out.println("非纯色");
                			}
                        	if(notPure){
                        		String newFileName = fileNamePrefix + "_" + startHight + pngPostfix;
                            	ImageIO.write(newImage, "png", new File(newFileName));
                        	}
                        	
                        }
                        
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }  
                    // 退出操作  
                    System.exit(0);
                }  
            }  
        }  
        );
        add(panel, BorderLayout.SOUTH);
    }  
    public static void main(String[] args) {  
        NativeInterface.open();  
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {  
                // SWT组件转Swing组件，不初始化父窗体将无法启动webBrowser  
                JFrame frame = new JFrame("以DJ组件保存指定网页截图");
                // 加载指定页面，最大保存为640x480的截图  
                frame.getContentPane().add(
                        new ScreenshotUtils("file:///H:/temp/html2images/10616.html", 1326, 720),
                		//new ScreenshotUtils("http://ask.testfan.cn/question/277", 1280, 720),
                		BorderLayout.CENTER);
                frame.setSize(1326, 720);
                // 仅初始化，但不显示  
                frame.invalidate();
                frame.pack();
                frame.setVisible(false);
            }
        });
        
        NativeInterface.runEventPump();
        NativeInterface.close();
    }
}