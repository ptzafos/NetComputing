import java.io.Serializable;

public class ReportObject implements Serializable {
	private float memoryusage;
    private float cpuusage;

    public ReportObject(float memoryusage, float cpuusage) {
        this.memoryusage = memoryusage;
        this.cpuusage = cpuusage;
    }

	public float getMemoryusage() {
		return memoryusage;
	}

	public float getCpuusage() {
		return cpuusage;
	}
}
