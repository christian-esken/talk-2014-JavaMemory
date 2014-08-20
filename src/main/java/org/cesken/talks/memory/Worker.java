/*********************************************************************************
 * Copyright 2014-present Christian Esken
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **********************************************************************************/

package org.cesken.talks.memory;

import java.lang.management.ManagementFactory;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author cesken
 *
 */
public class Worker implements Callable<String>
{
	static int workCounter = 0;
	static int errorCount = 0;
	static final int ROUNDS = 10_000;
	
	String name = StringUtils.repeat("Worker".concat(Integer.toString(workCounter)), 10000);
	
	enum HousePart
	{
		Door, Window, Roof, Floor, Plumbing;

		Set<Callable<String>> workers = new HashSet<>();

		void register(Callable<String> w)
		{
			workers.add(w);
		}

		void unregister(Callable<String> w)
		{
			workers.remove(w);
		}
	}

	void work(HousePart housePart) throws Exception
	{
		housePart.register(this);
		call(); // do the work
		housePart.unregister(this);
	}

		
	public static void main(String[] args)
	{
		System.out.println("Working ... on creating a memory leak");
		System.out.println("JVM ID: " + ManagementFactory.getRuntimeMXBean().getName());
		for (int i=0; i<ROUNDS; i++)
		{
			try
			{
				Worker worker = new Worker();
				worker.work(HousePart.Door);
			}
			catch(Exception exc)
			{
				errorCount ++;
				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e)	{} // ignore
			}
		}
		System.out.println("Work done. errorCount=" + errorCount);
	}


	@Override
	public String call() throws Exception
	{
		if ((workCounter++ %10) == 0)
		{
			throw new RuntimeException("Feeling ill");
		}
			
		return "Work done";
	}
}
