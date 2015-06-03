/* (c) 2014 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.
 */

package gobblin.example.simplejson;

import java.io.IOException;
import java.io.File;
import java.io.FileWriter;

import java.util.List;
import java.util.Iterator;
import java.util.Date;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import gobblin.configuration.ConfigurationKeys;
import gobblin.configuration.SourceState;
import gobblin.configuration.WorkUnitState;
import gobblin.source.Source;
import gobblin.source.extractor.Extractor;
import gobblin.source.workunit.Extract;
import gobblin.source.workunit.WorkUnit;

import org.apache.commons.io.FileUtils;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.Files;
import java.nio.file.Path;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import java.net.*;
/**
 * An implementation of {@link Source} for the simple JSON example.
 *
 * <p>
 *   This source creates one {@link gobblin.source.workunit.WorkUnit}
 *   for each file to pull and uses the {@link SimpleJsonExtractor} to pull the data.
 * </p>
 *
 * @author ynli
 */
@SuppressWarnings("unused")
public class SimpleJsonSource implements Source<String, String> {

  private static final String SOURCE_FILE_KEY = "source.file";

  @Override
  public List<WorkUnit> getWorkunits(SourceState state) {
    List<WorkUnit> workUnits = Lists.newArrayList();

    if (!state.contains(ConfigurationKeys.SOURCE_FILEBASED_FILES_TO_PULL)) {
      return workUnits;
    }

    // Create a single snapshot-type extract for all files
    Extract extract = new Extract(state, Extract.TableType.SNAPSHOT_ONLY,
        state.getProp(ConfigurationKeys.EXTRACT_NAMESPACE_NAME_KEY, "ExampleNamespace"), "ExampleTable");

    String filesToPull = state.getProp(ConfigurationKeys.SOURCE_FILEBASED_FILES_TO_PULL);
    File tempFileDir = new File("test_temp/"); // TODO: Delete the dir after completion.
    tempFileDir.mkdir();
    String tempFileDirAbsolute = "";
    try{
      tempFileDirAbsolute = tempFileDir.getCanonicalPath(); // Retrieve absolute path of temp folder
    } catch(IOException e){
        e.printStackTrace();
    }

    int nameCount = 0;
    int csvCount = 0;
    for (String file : Splitter.on(',').omitEmptyStrings().split(filesToPull)) {
      Iterator it = FileUtils.iterateFiles(new File(file), null, true);
      while(it.hasNext()) {
        try{
          File newFile = (File) it.next();
          String basePath = newFile.getCanonicalPath(); // Retrieve absolute path of source
          Path path = newFile.toPath();

          //call to rest api:, provide with file basePath
          String extension = "";
          System.out.println("basePath is" + basePath);
          int i = basePath.lastIndexOf('.');
          System.out.println("i");
          if (i > 0) {
              extension = basePath.substring(i+1);
          }

          String url_file_name = "";
          int j = basePath.lastIndexOf('/');
          if (j > 0) {
            url_file_name = basePath.substring(j+1);
          }

          //hand off to rest api
          if (extension.equals("csv")) {
            System.out.println("CSVCSVCSV");
            csvCount+=1;
            System.out.println("CURL________________________________________");
            //Include basePath, filename, location you want to store file
            System.out.println("curl http://localhost:8080" + basePath + "/" + Integer.toString(nameCount));
            //10.0.2.2 is localhost from vagrant
            // Insert the nameCount so that it can be joined back later.
            Process p = Runtime.getRuntime().exec("curl http://localhost:8080" + basePath + "/" + Integer.toString(nameCount));
            // String myUrl = "http://localhost:8080/parse" + basePath + "&" + url_file_name + "&" + tempFileDirAbsolute;
            // System.out.println("------------------------------");
            // System.out.println(myUrl);
            // try {
            //   URL url = new URL(myUrl);
            //   HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //   connection.setRequestMethod("GET");
            //   connection.connect();
            // } catch (Exception e) {
            //   e.printStackTrace();
            // }
          

          } 
          // Print filename and associated metadata 
          System.out.println(basePath);
          BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
          // System.out.println("  creationTime: " + attr.creationTime());
          // System.out.println("  lastAccessTime: " + attr.lastAccessTime());
          // System.out.println("  lastModifiedTime: " + attr.lastModifiedTime());
          // System.out.println("  isDirectory: " + attr.isDirectory());
          // System.out.println("  isOther: " + attr.isOther());
          // System.out.println("  isRegularFile: " + attr.isRegularFile());
          // System.out.println("  isSymbolicLink: " + attr.isSymbolicLink());
          // System.out.println("  size: " + attr.size()); 
          // System.out.println(" ");

          //creating intermediate JSON
          JSONObject intermediate = new JSONObject();
          intermediate.put("filename", basePath);
          intermediate.put("timestamp", String.valueOf((new Date()).getTime()));
          intermediate.put("namespace", getMacAddress());

          intermediate.put("creationTime", String.valueOf(attr.creationTime()));
          intermediate.put("lastAccessTime", String.valueOf(attr.lastAccessTime()));
          intermediate.put("lastModifiedTime", String.valueOf(attr.lastModifiedTime()));
          intermediate.put("isDirectory", String.valueOf(attr.isDirectory()));
          intermediate.put("isOther", String.valueOf(attr.isOther()));
          intermediate.put("isRegularFile", String.valueOf(attr.isRegularFile()));
          intermediate.put("isSymbolicLink", String.valueOf(attr.isSymbolicLink()));
          intermediate.put("size", attr.size());

    // Create intermediate temp file
          nameCount += 1;
          String intermediateName = "/generated" + String.valueOf(nameCount) + ".json";
          String finalName = tempFileDirAbsolute + intermediateName;
          FileWriter generated = new FileWriter(finalName);
          generated.write(intermediate.toJSONString());
          generated.flush();
          generated.close();

          // Create one work unit for each file to pull
          WorkUnit workUnit = new WorkUnit(state, extract);
          workUnit.setProp(SOURCE_FILE_KEY, finalName);
          workUnits.add(workUnit);
        }catch(IOException e){
            e.printStackTrace();
        }
      }

      // write out number of files found to temp file
      try {
        FileWriter numCsvFiles = new FileWriter(tempFileDirAbsolute + "/numCsvFiles.txt");
        numCsvFiles.write("" + csvCount);
        numCsvFiles.flush();
        numCsvFiles.close();

        FileWriter numFiles = new FileWriter(tempFileDirAbsolute + "/numFiles.txt");
        numFiles.write("" + nameCount);
        numFiles.flush();
        numFiles.close();
      }catch(IOException e){
          e.printStackTrace();
      }

    }

    return workUnits;
  }

  @Override
  public Extractor<String, String> getExtractor(WorkUnitState state)
      throws IOException {
    return new SimpleJsonExtractor(state);
  }

  @Override
  public void shutdown(SourceState state) {
    // Nothing to do
  }

  private String getMacAddress() {
    //InetAddress ip;
    //String mac_string = "default";
    //try {
    //  ip = InetAddress.getLocalHost();
    //  NetworkInterface network = NetworkInterface.getByInetAddress(ip);
    //  byte[] mac = network.getHardwareAddress();
    //  mac_string = mac.toString();
    //} catch (UnknownHostException e) {
    //  e.printStackTrace();
    //} catch (SocketException e){
    //  e.printStackTrace();
    //}
    String mac_string = "default";
    return mac_string;
  }
}
