package org.geotools.process.raster;

import java.awt.Point;
import java.awt.RenderingHints;
import java.io.File;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;

import junit.framework.Assert;

import org.geotools.process.raster.changematrix.ChangeMatrixDescriptor.ChangeMatrix;
import org.junit.Ignore;
import org.junit.Test;

public class ChangeMatrixTest extends Assert {
	
	/**
	 * No exceptions if the SPI is properly registered
	 */
	@Test
	@Ignore
	public void testSPI(){
		new ParameterBlockJAI("ChangeMatrix");
		
	}
	
	@Test
	public void basicTest() throws Exception{		
		
		final ParameterBlockJAI pbj=new ParameterBlockJAI("ChangeMatrix");
		final Set<Integer> classes = new HashSet<Integer>();
		classes.add(0);
		classes.add(1);
		classes.add(35);
		classes.add(36);
		classes.add(37);
		final ChangeMatrix cm= new ChangeMatrix(classes);
		
		final RenderedOp source= JAI.create("ImageRead",new File("d:/data/unina/clc2006_L3_100m.tif"));
		final RenderedOp reference=  JAI.create("ImageRead",new File("d:/data/unina/clc2000_L3_100m.tif"));
		
		final ImageLayout layout= new ImageLayout();
		layout.setTileHeight(256).setTileHeight(256);
		final RenderingHints hints= new RenderingHints(JAI.KEY_IMAGE_LAYOUT, layout);
		
		pbj.addSource(source);
		pbj.setParameter("bandSource", 0);
		pbj.setParameter("bandReference", 0);
		pbj.setParameter("referenceImage", reference);
		pbj.setParameter("result", cm);
		final RenderedOp result = JAI.create("ChangeMatrix", pbj,hints);
		result.getWidth();
		
		final Queue<Point> tiles= new ArrayBlockingQueue<Point>(result.getNumXTiles()*result.getNumYTiles());
		for(int i=0;i<result.getNumXTiles();i++){
			for(int j=0;j<result.getNumYTiles();j++){
				tiles.add(new Point(i,j));
			}
		}
		final CountDownLatch sem= new CountDownLatch(result.getNumXTiles()*result.getNumYTiles());
		ExecutorService ex = Executors.newFixedThreadPool(5);
		for(final Point tile:tiles){
			ex.execute(new Runnable() {
				
				@Override
				public void run() {
					result.getTile(tile.x, tile.y);
					sem.countDown();
				}
			});
		}
		sem.await();
		result.dispose();
		source.dispose();
		reference.dispose();
		
		// spit out results
		for(Integer ref: classes){
			for(Integer now: classes){
				System.out.println("["+ref+","+now+"]("+cm.retrievePairOccurrences(ref, now)+")");
			}
		}
		
		
		
		
	}

}

