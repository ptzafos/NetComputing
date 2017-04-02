import java.io.Serializable;

public class ReportObject implements Serializable {
	private float memoryusage;
    private float cpuusage;
    private int clientID;
    public ReportObject(float memoryusage, float cpuusage,int clientID) {
        this.memoryusage = memoryusage;
        this.cpuusage = cpuusage;
        this.clientID = clientID;
    }

	public float getMemoryusage() {
		return memoryusage;
	}

	public float getCpuusage() {
		return cpuusage;
	}

	public int getClientID() {
		return clientID;
	}
}
