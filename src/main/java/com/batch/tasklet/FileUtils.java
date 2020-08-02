package com.batch.tasklet;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.batch.db.Record;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class FileUtils {

	private final Logger logger = LoggerFactory.getLogger(FileUtils.class);

	private String fileName;
	private CSVReader reader;
	private CSVWriter writer;
	private FileReader fileReader;
	private FileWriter fileWriter;
	private File file;

	public FileUtils(String fileName) {
		this.fileName = fileName;
	}

	public Record readLine() {
		try {
			if (reader == null)
				initReader();
			String[] line = reader.readNext();
			if (line == null)
				return null;
			return new Record(Integer.parseInt(line[0]), line[1],line[2]);
		} catch (Exception e) {
			logger.error("Error while reading line in file: " + this.fileName);
			return null;
		}
	}

	/*
	 * public void writeLine(Line line) { try { if (CSVWriter == null) initWriter();
	 * String[] lineStr = new String[2]; lineStr[0] = line.getName(); lineStr[1] =
	 * line.getAge().toString(); CSVWriter.writeNext(lineStr); } catch (Exception e)
	 * { logger.error("Error while writing line in file: " + this.fileName); } }
	 */
	private void initReader() throws Exception {
		ClassLoader classLoader = this.getClass().getClassLoader();
		if (file == null)
			//file = new File(classLoader.getResource(fileName).getFile());
			file = new File(fileName);
		if (fileReader == null)
			fileReader = new FileReader(file);
		if (reader == null)
			reader = new CSVReader(fileReader);
	}

	private void initWriter() throws Exception {
		if (file == null) {
			file = new File(fileName);
			file.createNewFile();
		}
		if (fileWriter == null)
			fileWriter = new FileWriter(file, true);
		if (writer == null)
			writer = new CSVWriter(fileWriter);
	}

	public void closeWriter() {
		try {
			writer.close();
			fileWriter.close();
		} catch (IOException e) {
			logger.error("Error while closing writer.");
		}
	}

	public void closeReader() {
		try {
			if (reader != null)
				reader.close();
			if (fileReader != null)
				fileReader.close();
		} catch (IOException e) {
			logger.error("Error while closing reader.");
		}
	}

}