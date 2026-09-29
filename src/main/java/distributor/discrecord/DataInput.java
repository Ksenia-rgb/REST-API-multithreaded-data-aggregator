package distributor.discrecord;

public record DataInput(ApiInfo[] apis,
                        String format,
                        String path,
                        boolean append,
                        String output,
                        int threads,
                        int period,
                        int count
)
{
    public DataInput withAppend(boolean appendNew)
    {
        return new DataInput(apis, format, path, appendNew, output, threads, period, count);
    }
}