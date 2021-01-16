import java.io.*;
import java.util.*;
import java.nio.charset.Charset;

class mergeFiles {
  public mergeFiles() {

  }

  /**
  * reads text files from a folder.
  *
  * @param folder where data should be read.
  * @return List of Files.
  *
  */
  public List<File> readFilesFromFolder(String folder) {
    List<File> flist = new ArrayList<>();
    File mydir = new File(folder);
    String[] filenames = mydir.list();
    for (String filename: filenames) {
      System.out.println(filename);
      File myfile = new File(mydir, filename);
      flist.add(myfile);
    }
    return flist;
  }

  public static Comparator<String> stringcomparator = new Comparator<String>() {
    @Override
    public int compare(String r1, String r2) {
            return r1.compareTo(r2);
    }
  };

  /**
  * merges several BufferedReaderWrapper to an outputfile.
  *
  * @param buffers where data should be read.
  * @param outputfilename where sorted data should be written
  * @throws IOException generic IO exception
  *
  */
  public void mergeSortedBuffers(List<DataStack> buffers, String outputfilename) throws IOException {
    boolean append = true;
    final Comparator<String> cmp = stringcomparator;
    PriorityQueue<DataStack> pQueue = new PriorityQueue<>(
      10,
      new Comparator<DataStack>() {
            @Override
            public int compare(DataStack st1, DataStack st2) {
                return cmp.compare(st1.peek(), st2.peek());
            }
    });

    BufferedWriter fbWriter = new BufferedWriter(new OutputStreamWriter(
                             new FileOutputStream(new File(outputfilename), append), Charset.defaultCharset()));

    for (DataStack buffer : buffers) {
      if (!buffer.empty()) {
              pQueue.add(buffer);
      }
    }

    try {
      String lastLine = null;
      if(pQueue.size() > 0) {
         DataStack buffer = pQueue.poll();
         lastLine = buffer.pop();
         if (!lastLine.isEmpty()) {  //ignore empty line
           fbWriter.write(lastLine);
           fbWriter.newLine();
         }

         if (buffer.empty()) {
           buffer.close();
         } else {
           pQueue.add(buffer); // add it back to pQueue
         }
      }
      while (pQueue.size() > 0) {
          DataStack buffer = pQueue.poll();
            String r = buffer.pop();
            // ignore empty line and skip duplicate lines
            if  (!r.isEmpty() && cmp.compare(r, lastLine) != 0) {
              fbWriter.write(r);
              fbWriter.newLine();
              lastLine = r;
            }

            if (buffer.empty()) {
              buffer.close();
            } else {
              pQueue.add(buffer); // add it back to pQueue
            }
      }
    } finally {
            fbWriter.close();
            for (DataStack buffer : pQueue) {
                    buffer.close();
            }
    }
  }


  /**
  * reads text files into BufferedReader and calls merge function.
  *
  * @param files where data should be read.
  * @param outputfilename where sorted data should be written
  * @throws IOException generic IO exception
  *
  */
  public void mergeSortedFiles(List<File> files, String outputfilename) throws IOException {
    ArrayList<DataStack> buffers = new ArrayList<>();

    for (File myfile : files) {
      final int BUFFERSIZE = 2048;
      if (myfile.length() == 0) {
              continue;
      }
      InputStream in = new FileInputStream(myfile);

      BufferedReader br = new BufferedReader(new InputStreamReader(in, Charset.defaultCharset()));
      buffers.add(new BufferedReaderWrapper(br));
    }

    mergeSortedBuffers(buffers, outputfilename);
  }

  public void readandmerge(String folder, String outputfilename) throws IOException{
    List<File> fList = readFilesFromFolder(folder);
    mergeSortedFiles(fList, outputfilename);
  }

  private static void help() {
    System.out.println("java mergeFiles inputdir outputfile");
  }

  public static void main(String[] args) throws IOException{
    if (args.length < 2) {
      help();
    }
    else {
      String folder = args[0];
      String outputfilename = args[1];
      mergeFiles mf = new mergeFiles();
      mf.readandmerge(folder, outputfilename);
    }
  }
}
