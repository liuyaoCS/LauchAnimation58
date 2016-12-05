package com.example.demolaunchleading;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.Region.Op;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;
/**
 * 
 * @author ljq ly hyz
 *
 */
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback{
	public static String TAG ="test";
	private SurfaceHolder holder;
	MyThread thread;
	Context mcontext;
	private float dis;
	GestureDetectorCompat mCompat;
	Scroller mScroller;
	
	boolean isend=false;
	boolean isBarUpFromActiveMoving=false;
	///////////////for progress bar////////////////////////////
	private Paint bgPaint;
	private Paint fgPaint;
	private float bgThickness = 5;
	private float fgThickness = 35;
	
	private Rect rect;
	float l=0;
	float t=0;
	float r=0;
	float b=0;
	float Radius=(r-l)/2;
	private PointF centerPoint = null;
	private double degree;
	
	private Drawable progress;
	private Bitmap progressbk;
	private Path path;
	
	float x=0;
	float y=0;
	float down_x=0;
	float down_y=0;
	
	Bitmap bitmap,barbk;
	int bitmapWidth=0;
	int bitmapHeight=0;
	
	private PointF thumbPoint=null;
	
	float a=0;
	////////////////for sliding text//////////////////////
	private Rect rectText;
	private Path pathText;

	int lt=0;
	int rt=0;
	int tt=0;
	int bt=0;
	private Drawable sliding;

	int loc=0;
	///////////////////////////////////////////
	int original = 0;
	int count=0;
	@SuppressWarnings("static-access")
	public MySurfaceView(Context context) {
		super(context);
        holder = this.getHolder();
        holder.addCallback(this);
        this.mcontext=context;

        
        mScroller = new Scroller(context, new LinearInterpolator());
        
		mCompat = new GestureDetectorCompat(context, new GestureDetector.OnGestureListener()
		{


			@Override
			public boolean onDown(MotionEvent e) {
				
				return false;
			}

			@Override
			public void onShowPress(MotionEvent e) {
				
			}

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				
				return false;
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {
				 dis =distanceX;
//				if(Math.abs(dis) > 2)
//				{
					thread.setDis(-dis);
					thread.draw(true);
					Log.i("bug", "[onScroll] dis:"+dis);
//				}
				return false;
			}

			@Override
			public void onLongPress(MotionEvent e) {
				
			}

//			@Override
//			public boolean onFling(MotionEvent e1, MotionEvent e2,
//					float velocityX, float velocityY) {
//				return false;
//			}
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
//				右移动 velocitX正，dis负
				
				int absVelocityX = (int)Math.abs(velocityX);
				int times = 30;
			
				float dis = 0;
				if (absVelocityX<1000){
					dis = 0f;
				}else if(absVelocityX<3000){
					dis = 40f;
				}else if(absVelocityX<5000){
					dis = 80f;
				}else{
					dis = 100f;
				}
				if(velocityX>0){
					dis = -dis;
				}
				while(times>0){
					//Log.d(TAG, "absVelocityX:"+absVelocityX+"  dis"+dis);

					dis *= 0.9;
					thread.setDis(-dis);
					thread.draw(true);
					times--;
					try {
						Thread.currentThread().sleep(6);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Log.i("bug", "[onfling ] dis,absVelocityX: "+dis+" ,"+Math.abs(velocityX));
			
				return false;
			}

			
		});
	}
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {	 
		 if(isend){
			 switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					//this.isbar_up=false;
					down_x = event.getX();
					down_y = event.getY();
					Log.i("progress", "down-event");
					break;
					
				case MotionEvent.ACTION_MOVE:
					
					x=event.getX();
					y = event.getY();
					if(!((Math.abs(x-down_x)>1 && Math.abs(y-down_y)>1)) || !isInValidRegion(x,y))break;
					
					this.isBarUpFromActiveMoving=false;
					degree = Math.toDegrees(Math.atan((y - centerPoint.y)
							/ (x - centerPoint.x)));
					path=DegreeToPath(degree);
					thumbPoint.x=x;
					thumbPoint.y=fixThumbPosition_y(x);
					if(x>=r-bitmapWidth/4){
						Intent intent=new Intent();
						intent.setClass(this.mcontext, OtherActivity.class);
						this.mcontext.startActivity(intent);
						if(thread!=null){
							thread.isRunning = false;
							try {
					            thread.join();
					        } catch (InterruptedException e) {
					            e.printStackTrace();
					        }
					        
					        thread = null;
						}
						((MainActivity)this.mcontext).finish();
					}
					invalidate();
					Log.i("progress", "move-event,x: "+x+" y: "+y);
					break;
				case MotionEvent.ACTION_UP:
					path=DegreeToPath(0);
					thumbPoint.x=l+this.bitmapWidth/4;
					thumbPoint.y=fixThumbPosition_y(l+this.bitmapWidth/4);
					this.isBarUpFromActiveMoving=true;
					invalidate();
					Log.i("progress", "up-event");
				}
		 }
		 if(!isend || this.isBarUpFromActiveMoving)mCompat.onTouchEvent(event);
		 //mCompat.onTouchEvent(event);
		 return true;
	}


	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.v("activityLY", "surfaceCreated");
		thread = new MyThread(holder,mcontext);
		thread.isRunning = true;  
        thread.start();  
        
        updateTask=new MyRunnable(holder);
        //handler.post(updateTask);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.v("activityLY", "surfaceChanged");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.v("activityLY", "surfaceDestroyed");
		Log.i("thread", "surfaceDestroyed :kill thread");
		if(thread!=null){
			thread.isRunning = false;
			try {
	            thread.join();
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	        
	        thread = null;
		}
		
 
	}
	private void init() {
		bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		bgPaint.setStyle(Style.STROKE);
		bgPaint.setStrokeWidth(bgThickness);
		bgPaint.setColor(Color.BLACK);
		BlurMaskFilter blurMaskFilter = new BlurMaskFilter(1, Blur.INNER);
		bgPaint.setMaskFilter(blurMaskFilter);
		

		fgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		fgPaint.setStyle(Style.STROKE);
		fgPaint.setStrokeWidth(fgThickness);
		fgPaint.setColor(Color.RED);
		BlurMaskFilter blurMaskFilter2 = new BlurMaskFilter(1, Blur.OUTER);
		fgPaint.setMaskFilter(blurMaskFilter2);
		
				
		progress = getResources().getDrawable(R.drawable.progress);
		path=new Path();
		bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.thumb);
		bitmapWidth=bitmap.getWidth();
		bitmapHeight=bitmap.getHeight();
		
		progressbk=BitmapFactory.decodeResource(getResources(),R.drawable.progressbk);	
		
		rect=new Rect(0,0,0,0);
		centerPoint = new PointF((l + r)/2,  t);
		a=-(b-t-bitmapHeight/4)/(Radius*Radius);
		thumbPoint=new PointF(l+this.bitmapWidth/4,fixThumbPosition_y(l+this.bitmapWidth/4));
		
		/////////////for sliding/////////
		sliding=getResources().getDrawable(R.drawable.sliding);
		pathText=new Path();
		rectText=new Rect(lt,tt,rt,bt);
		loc=lt;
		/////////////////////////////
	}
	public Path DegreeToPath(double degree){
		Path path=new Path();
		double degree1=Math.toDegrees(Math.atan((b-centerPoint.y)/(r-centerPoint.x)));
		double degree2=180-degree1;
		if(degree<=0){
			degree=Math.abs(degree);
			degree=degree/180*Math.PI;
			//Log.i("progress", Math.tan(3.1415926/2)+"");
			if(degree<degree1){
				int y=(int) ((Math.tan(degree))*(centerPoint.x-l)+t);
				path.moveTo(l, t);
				path.lineTo(centerPoint.x, t);
				path.lineTo(l, y);			
				path.lineTo(l, t);
				Log.i("progress", "degree="+degree+"down:x,y"+down_x+","+down_y+" case 1, x,y= "+l+","+y);
			}else{
				int x=(int) (centerPoint.x-(Math.tan(90-degree))*(b-t));
				path.moveTo(l, t);
				path.lineTo(centerPoint.x, t);
				path.lineTo(x, b);	
				path.lineTo(l, b);	
				path.lineTo(l, t);
				Log.i("progress ", "degree="+degree+"down:x,y"+down_x+","+down_y+"case 2, x,y= "+x+","+b);
			}
		}else{
			degree=degree/180*Math.PI;
			if(degree>degree2){
				int x=(int) ((Math.tan(90-degree))*(b-t)+centerPoint.x);
				path.moveTo(l, t);
				path.lineTo(centerPoint.x, t);
				path.lineTo(x, b);	
				path.lineTo(l, b);	
				path.lineTo(l, t);
				Log.i("progress", "degree="+degree+"down:x,y"+down_x+","+down_y+"case 3, x,y= "+x+","+b);
			}else{
				int y=(int) ((Math.tan(degree))*(centerPoint.x-l)+t);
				path.moveTo(l, t);
				path.lineTo(centerPoint.x, t);
				path.lineTo(r, y);
				path.lineTo(r, b);	
				path.lineTo(l, b);
				path.lineTo(l, t);
				Log.i("progress", "degree="+degree+"down:x,y"+down_x+","+down_y+"case 1, x,y= "+r+","+y);
			}
		}
		return path;
	}
	public float fixThumbPosition_y(float x){
		float ret=t;
		ret=a*(x-l)*(x-l-2*Radius)+t;
		return ret;
	}
	public boolean isInValidRegion(float x,float y){
		float ypath=fixThumbPosition_y(x);
		if((x>=l && x<=r && y>=t && y<=b) && Math.abs(y-ypath)<this.bitmapHeight/2 && (down_x<=l+this.bitmapWidth/2 && down_x>=l))return true;
		else return false;
	}
	//////////for progress text////////////////////
	Handler handler=new Handler();
	Runnable updateTask;
	class MyRunnable implements Runnable{
		SurfaceHolder surfaceHolder;
		MyRunnable(SurfaceHolder surfaceHolder){
			this.surfaceHolder = surfaceHolder;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Canvas c = null;
			c = surfaceHolder.lockCanvas(null);
			updateSlidingWhiteTextLoc(c);
			if(c!=null)surfaceHolder.unlockCanvasAndPost(c);
			
			handler.postDelayed(updateTask, 150);
		}
		
	}
	public void updateSlidingRectTextLoc(){
		lt=(int) (l+(r-l)/3-(r-l)/15);
    	rt=(int) (l+(r-l)/3*2);
    	tt=(int) (t+(b-t)*9/12);
    	bt=(int) (t+(b-t)*11/12);
    	rectText.left=lt;
    	rectText.right=rt;
    	rectText.top=tt;
    	rectText.bottom=bt;
	}
	public void updateSlidingWhiteTextLoc(Canvas c){
    	if(c==null)return;
    	if(loc>rt){
    		loc=lt;
//    		pathText.reset();
//    		c.clipPath(pathText);
    	}
        else loc+=10;
    }
	///////////////////////////
    class MyThread extends Thread{
 
        SurfaceHolder surfaceHolder;
        Context context;
        boolean isRunning;
        Paint paint;
        int x=0,v=0;
        
        float distance;
        Bitmap mbit;
        Bitmap bk;
        Bitmap mSun;
        Bitmap mBoy1;
        Bitmap mBoy2;
        Bitmap mBoy3;
        Bitmap mBoy4;
        Bitmap mBoy5;
        Bitmap mStreet;
        
        Bitmap car1;
        Bitmap car2;
        Bitmap car3;
        Bitmap car4;
        
        Bitmap build1;
        Bitmap build2;
        Bitmap build3;
        Bitmap build4;
        
        Bitmap loadingText1;
        Bitmap loadingText2;
        Bitmap loadingText3;
        Bitmap loadingText4;
        Bitmap loadingText5;
        
        Bitmap cloud;
        
        Bitmap arrow1,arrow2;
        private boolean isArrow1 = true;
        private int arrowPeriod = 5;
        private boolean isArrowZoomed = false;
        
        int initWidth;
        int initHeight;
        
        //int original = 0;
        int i = 10;
        int k=0;
        int MAX = 0;
        
        public void setDis(float dis)
        {
        	this.distance = 2*dis;
        }
        
        boolean isDraw = true;
        public void draw(boolean flag)
        {
        	isDraw = flag;
        }
 
        public MyThread(SurfaceHolder surfaceHolder,Context context){
 
            this.surfaceHolder = surfaceHolder;
        
            mbit = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
            initWidth = mbit.getWidth();
            initHeight = mbit.getHeight();
            
            mSun = BitmapFactory.decodeResource(context.getResources(), R.drawable.sun);
            
            mBoy1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.boy1);
            mBoy2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.boy2);
            mBoy3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.boy3);
            mBoy4 = BitmapFactory.decodeResource(context.getResources(), R.drawable.boy4);
            mBoy5 = BitmapFactory.decodeResource(context.getResources(), R.drawable.boy5);
            mStreet = BitmapFactory.decodeResource(context.getResources(), R.drawable.street);
            
            car1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.car1);
            car2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.car2);
            car3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.car3);
            car4 = BitmapFactory.decodeResource(context.getResources(), R.drawable.car4);
            
            build1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.build1);
            build2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.build2);
            build3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.build3);
            build4 = BitmapFactory.decodeResource(context.getResources(), R.drawable.build4);
            
            
            loadingText1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.leading_text_1);
            loadingText2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.leading_text_2);
            loadingText3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.leading_text_3);
            loadingText4 = BitmapFactory.decodeResource(context.getResources(), R.drawable.leading_text_4);
            loadingText5 = BitmapFactory.decodeResource(context.getResources(), R.drawable.leading_text_5);
            
            
            cloud = BitmapFactory.decodeResource(context.getResources(), R.drawable.cloud);
            
            arrow1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow1);
            arrow2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow2);
            

    
            
            MAX = - initWidth * 8-initWidth/2;
            
            paint = new Paint();
            paint .setStyle( Paint.Style.STROKE );   //空心
            
            init();
           
        }
 
        @Override
        public void run() {
 
            Canvas c = null;
            while(isRunning)
            {
            	Log.i("thread", " thread is alive");
                try{
                    synchronized (surfaceHolder) {
                    	c = surfaceHolder.lockCanvas(null);
                    if(isDraw)
                    {
                    	if((original + distance) < 0 && (original + distance) > MAX)
                    	{
                    		updateProgressLoc();
                    	}
                        isDraw = false;
                    }

                    updateSlidingRectTextLoc();
                    if(count==1){
                    	updateSlidingWhiteTextLoc(c);
                    	count=0;
                    }else count++;
                    doDraw(c);
                    if(c!=null)surfaceHolder.unlockCanvasAndPost(c);

                    Thread.sleep(5);
                    }
                   } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                    }
            }
 
        }
        
        public void updateProgressLoc(){
    	    original += distance;
    	 
		    l=original +8*initWidth+initWidth/2+Constants.WINWIDTH/10;
     		int tmp=(int)l;
     		isend=(Math.abs(tmp-Constants.WINWIDTH/10)<=5)?true:false;
     	
     		r=l+Constants.WINWIDTH*4/5;
     		t=Constants.WINHEIGHT/2;
     		b=t+Constants.WINWIDTH*2/5;
         	rect.left=(int) l;
     		rect.right=(int) r;
     		rect.top=(int) t;
     		rect.bottom=(int) b;
     		
         	Radius=(r-l)/2;
         	centerPoint = new PointF((l + r)/2,  t);
     		a=-(b-t-bitmapHeight/4)/(Radius*Radius);
     		thumbPoint=new PointF(l+bitmapWidth/4,fixThumbPosition_y(l+bitmapWidth/4));
  		
        }
//        public void updateTextLoc(Canvas c){
//        	if(c==null)return;
//        	lt=(int) (l+(r-l)/3);
//        	rt=(int) (l+(r-l)/3*2);
//        	tt=(int) (t+(b-t)*9/12);
//        	bt=(int) (t+(b-t)*11/12);
//        	rectText.left=lt;
//        	rectText.right=rt;
//        	rectText.top=tt;
//        	rectText.bottom=bt;
//     		
//        	if(loc>rt){
//        		loc=lt;
//        		pathText.reset();
//        		c.clipPath(pathText);
//        	}
//            else loc+=10;
//        }
        public void doDraw(Canvas c){
        	if(c == null)
        	{
        		return;
        	}
   	
        	c.drawColor(Color.TRANSPARENT,Mode.CLEAR);
        	c.drawARGB(255, 178, 216, 255);
        	c.save();
        	c.rotate(i++, getWidth() - (mSun.getWidth()/2) , mSun.getHeight()/2);
        	c.drawBitmap(mSun, getWidth() - mSun.getWidth() ,0 , null);
        	c.restore();
        	c.save();
        	
        	/**
        	 * draw background
        	 */
        	for(int i = -1 ; i <= (getWidth() / initWidth) + 1 ; i++)
        	{
            	c.save();
            	c.drawBitmap(mbit, (original%initWidth)+i*initWidth,getHeight() - mStreet.getHeight() - initHeight , null);
            	c.restore();
        	}
        	
        	
        	/**
        	 * 
        	 * draw building
        	 * 
        	 */
        	
       	    {
        		c.save();
            	c.drawBitmap(build1,original ,getHeight() - mStreet.getHeight()-build1.getHeight(), null);
            	c.restore();
            	
        		c.save();
            	c.drawBitmap(build2,  original + 2*initWidth+initWidth/2,getHeight() - mStreet.getHeight()-build2.getHeight(), null);
            	c.restore();
            	
        		c.save();
            	c.drawBitmap(build3, original + 4*initWidth+initWidth/2,getHeight() - mStreet.getHeight()-build3.getHeight(), null);
            	c.restore();
            	
        		c.save();
            	c.drawBitmap(build4, original +6*initWidth+initWidth/2,getHeight() - mStreet.getHeight()-build4.getHeight(), null);
            	c.restore();    
            	
            	////for progress bar//////////////
            		
        		c.drawBitmap(progressbk, null, rect, null);
       
        		c.save();
        		c.clipPath(path); 
        		progress.setBounds(rect);
        		progress.draw(c);
        		c.restore();
        		
        		
        		//if(isend){
        			
    				c.save();
        			sliding.setAlpha(60);
        			sliding.setBounds(rectText);
            		sliding.draw(c);
            		c.restore();

        			
        			
        			c.save();
        			  
        			c.clipPath(pathText); 	
        			sliding.setAlpha(60);
        			sliding.setBounds(rectText);
            		sliding.draw(c);
            			
            		pathText.reset();
            		c.clipPath(pathText);
        			pathText.moveTo(loc, tt);
        			pathText.lineTo(loc+30, tt);
        			pathText.lineTo(loc+30, bt);
        			pathText.lineTo(loc, bt);
        			pathText.lineTo(loc, tt);
        			
            		c.clipPath(pathText,Op.REVERSE_DIFFERENCE); 
            		sliding.setAlpha(255);
            		sliding.setBounds(rectText);
            		sliding.draw(c);
                
                	c.restore();
        		//}
        		
            	
            	
        	
        		c.drawBitmap(bitmap, thumbPoint.x- bitmapWidth / 2,thumbPoint.y- bitmapHeight / 2, null);
		
        	}
        	
       	    /**
       	     * draw boy
       	     */
       	    c.save();
        	int k = (Math.abs(original)/50)%3;
        	Bitmap bit = mBoy1;
        	if(original+ 2*initWidth+initWidth/2>0)bit = k==0? mBoy1:mBoy2;
        	else if(original + 5*initWidth + initWidth/3*2>0)bit=k==0?mBoy3:mBoy4;
        	else if(original + 6*initWidth>0)bit=mBoy5;
        	else bit = k==0? mBoy1:mBoy2;
        	
        	
        	if(original + 7*initWidth+initWidth/2>0)c.drawBitmap(bit, getWidth()/2 -mBoy1.getWidth()/2,getHeight() - mStreet.getHeight() - mBoy1.getHeight()+5, null);
	       	else {
	       			
	       		 	x=(int) (255f+(float)(original +7*initWidth + initWidth/2)/((float)(Constants.WINWIDTH)/255f));
	        		v=(x<=255 && x>=0)?x:(x<0?0:255);
	                paint.setAlpha(v);   
	               
	                c.drawBitmap(bit, getWidth()/2 -mBoy1.getWidth()/2,getHeight() - mStreet.getHeight() - mBoy1.getHeight()+5, paint);
	       	 }
        	c.restore(); 
        	
        	/**
        	 * draw arrow
        	 */
        	
        	c.save();
        	if(!isArrowZoomed){
                arrow1 = zoomBitmap(arrow1, getWidth()/8, getWidth()/8);
                arrow2 = zoomBitmap(arrow2, getWidth()/8, getWidth()/8);
                isArrowZoomed = true;
        	}
        	if(original>=-5){
	        	if(isArrow1){

	        		c.drawBitmap(arrow1, getWidth()-arrow1.getWidth()+original-getWidth()/10, getHeight()-mStreet.getHeight()-mBoy1.getHeight()*4/5,null);
	        		
	        	}else{

	        		c.drawBitmap(arrow2, getWidth()-arrow2.getWidth()+original-getWidth()/10, getHeight()-mStreet.getHeight()-mBoy1.getHeight()*4/5,null);
	        		
	        	}
	        	if(arrowPeriod==0){
	        		isArrow1 = !isArrow1;
	        		arrowPeriod = 5;
	        	}else{
	        		arrowPeriod--;
	        	}
        	}
        	c.save();
          	

        	 /**
       	     * draw cloud
       	     */
        	 c.save();
        	 c.drawBitmap(cloud, getWidth()-mSun.getWidth()+original/16+20,mSun.getHeight()-cloud.getHeight()-5, null);       	
        	 c.restore();  
       	    
       	    /**
       	     * 5 draw text
       	     */
       	    {
        		c.save();
        		x=255-(original + initWidth/2);
        		v=(x<=255 && x>=0)?x:(x<0?0:255);
                paint.setAlpha(v);   
            	c.drawBitmap(loadingText1,original + initWidth/2,getHeight() -  initHeight - loadingText1.getHeight(), paint);
            	//Log.i("animation_text", "original : "+original+"       original + initWidth/2::"+(original + initWidth/2));
            	c.restore();
            	
        		c.save();
        		x=255-(original + 2*initWidth + initWidth/2);
        		v=(x<=255 && x>=0)?x:(x<0?0:255);
                paint.setAlpha(v);   
            	c.drawBitmap(loadingText2, original + 2*initWidth + initWidth/2,getHeight() -  initHeight - loadingText2.getHeight(), paint);
            	c.restore();
            	
        		c.save();
        		x=255-(original + 4*initWidth + initWidth/2);
        		v=(x<=255 && x>=0)?x:(x<0?0:255);
                paint.setAlpha(v); 
            	c.drawBitmap(loadingText3, original + 4*initWidth + initWidth/2,getHeight() -  initHeight - loadingText3.getHeight(), paint);
            	c.restore();
            	
        		c.save();
        		x=255-( original +6*initWidth + initWidth/2);
        		v=(x<=255 && x>=0)?x:(x<0?0:255);
                paint.setAlpha(v); 
            	c.drawBitmap(loadingText4, original +6*initWidth + initWidth/2,getHeight() -  initHeight - loadingText4.getHeight(), paint);
            	c.restore(); 
            	
            	c.save();
            	x=255-(original +8*initWidth + initWidth/2);
            	v=(x<=255 && x>=0)?x:(x<0?0:255);
                paint.setAlpha(v); 
                //Log.i("animation_text", original +8*initWidth + initWidth/2+""+" (x,v): "+x+" , "+v);
            	c.drawBitmap(loadingText5, original +8*initWidth + initWidth/2,getHeight() -  initHeight - loadingText5.getHeight(), paint);
            	c.restore();  
       	    }
       	    
       	    
       	   
       	    
       	    
        	/**
        	 * draw street
        	 */
        	int streetWidth = mStreet.getWidth();
        	for(int i = -1 ; i <= (getWidth() / streetWidth) + 1 ; i++)
        	{
        		c.save();
//            	c.translate(i*streetWidth , 0);
            	c.drawBitmap(mStreet, (original%streetWidth)+i*streetWidth,getHeight() - mStreet.getHeight(), null);
            	c.restore();
        	}
        	
        	/**
        	 * 4 draw car
        	 */
        	//180   540  1080 1620
        	{
        		
        		c.save();
            	c.drawBitmap(car1,original-original/8 +initWidth/2+getWidth()/3,getHeight() - mStreet.getHeight()-car1.getHeight()/3*2, null);
            	c.restore();
            	
        		c.save();
            	c.drawBitmap(car2, original-original/8-initWidth/4 + 2*initWidth +initWidth/2+ getWidth()/3,getHeight() - mStreet.getHeight()-car2.getHeight()/3*2, null);
            	c.restore();
            	
        		c.save();
            	c.drawBitmap(car3, original-original/8 -initWidth/2+ 4*initWidth+initWidth/2 + getWidth()/3,getHeight() - mStreet.getHeight()-car3.getHeight()/3*2, null);
            	c.restore();
            	
        		c.save();
            	c.drawBitmap(car4, original-original/8-3*initWidth/4 +6*initWidth+initWidth/2 + getWidth()/3,getHeight() - mStreet.getHeight()-car4.getHeight()/3*2, null);
            	c.restore();
            	
            	
        	}
        }
 
    }
    
	//图片缩放成指定的大小 
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {  
        int width = bitmap.getWidth();  
        int height = bitmap.getHeight();  
        Log.d(TAG, "zoomBitmap width:"+width+"zoomBitmap height:"+height);
        Matrix matrix = new Matrix();  
        float scaleWidth = ((float) w / width);  
        float scaleHeight = ((float) h / height);  
        matrix.postScale(scaleWidth, scaleHeight);  
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,matrix, true);  
        
        return newbmp;  
    } 
    public Path getTextPath(){
    	Path textPath=new Path();
    	float d=r-l;
    	
    	textPath.moveTo(l+d/3, this.fixThumbPosition_y(l+d/3));
    	for(int i=1;i<=5;i++)textPath.lineTo(l+d/3+d/12*i, this.fixThumbPosition_y(l+d/3+d/12*i));
    	
    	return textPath;
    }
}
