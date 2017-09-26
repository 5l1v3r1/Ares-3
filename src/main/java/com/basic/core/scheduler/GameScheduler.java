package com.basic.core.scheduler;

import com.basic.core.util.AresUtils;
import com.google.common.collect.Sets;
import org.apache.storm.scheduler.*;
import org.apache.storm.scheduler.resource.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class GameScheduler implements IScheduler {
    private static final Logger LOG = LoggerFactory.getLogger(EvenScheduler.class);


    //The parameter W1 and W2 denote the weight of the stream` response time and the stream recovery time, respectively;
    // public static double W1, W2;
    public static double W1 = 0.7, W2 = 0.3;
    //The parameter q denotes the computation cost for executors to process a single tuple.


    //The parameter lambda denotes the data processing time for slots to process a single computation cost.
//    public static Map<WorkerSlot, Double> lambda;

    //The parameter d denotes the data transferring time of node pairs.
 //   public static Map<WorkerSlot, Map<WorkerSlot, Double>> d;

    //The parameter w denotes the recover time of upstream and downstream executor pairs.
  //  public static Map<ExecutorDetails, Map<ExecutorDetails, Double>> w;

    //The parameter alpha is generated by transforming Problem 1 into Problem 2, and varies from executors to executors.
   // public static Map<ExecutorDetails, Double> alpha;

    //The parameter beta is generated by transforming Problem 1 into Problem 2, and varies from executor pairs to executor pairs.
  //  public static Map<ExecutorDetails, Map<ExecutorDetails, Double>> beta;

    //The parameter gamma is generated by transforming Problem 1 into Problem 2, and varies from executor pairs to executor pairs.
  //  public static Map<ExecutorDetails, Map<ExecutorDetails, Double>> gamma;

    public static void initializeWeights(double W3, double W4) {

    }

    public static Map<ExecutorDetails, Double> initializeQ(List<ExecutorDetails> executors) {
        Map<ExecutorDetails, Double> q = new HashMap<ExecutorDetails, Double>();
        for (ExecutorDetails executor : executors) {
            Random random = new Random();
            double cost = random.nextDouble();
            q.put(executor, cost);
        }
        return q;
    }

    public static Map<WorkerSlot, Double> initializeLambda(List<WorkerSlot> slots) {
        Map<WorkerSlot, Double> lambda = new HashMap<WorkerSlot, Double>();
        lambda.clear();
        for (WorkerSlot slot : slots) {
            Random random = new Random();
            double cost = random.nextDouble();
            lambda.put(slot, cost);
        }
        return lambda;
    }

    public static  Map<WorkerSlot, Map<WorkerSlot, Double>> initializeD(List<WorkerSlot> slots) {
        Map<WorkerSlot, Map<WorkerSlot, Double>> d = new HashMap<WorkerSlot, Map<WorkerSlot, Double>>();
     //   Map<WorkerSlot, Double> temp = new HashMap<WorkerSlot, Double>();
        for (int i = 0; i < slots.size(); i++) {
            for (int j = i; j < slots.size(); j++) {
                Random random = new Random();
                double cost = i == j ? 0.0 : random.nextDouble();
             //   temp.put(slots.get(j),cost);
              //  d.put(slots.get(i), temp);
               // WorkerSlot slot = slots.get(j);
                 //   temp.put(slots.get(j),0.0);
                  //  d.put(slots.get(i),temp);
                  //  d.get(slots.get(j)).put(slots.get(i), cost);

                if (d.containsKey(slots.get(i))) {
                    d.get(slots.get(i)).put(slots.get(j), cost);
                } else {
                    Map<WorkerSlot, Double> temp = new HashMap<WorkerSlot, Double>();
                    temp.put(slots.get(j), cost);
                    d.put(slots.get(i), temp);
                }
                if (d.containsKey(slots.get(j))) {
                    d.get(slots.get(j)).put(slots.get(i), cost);
                } else {
                    Map<WorkerSlot, Double> temp = new HashMap<WorkerSlot, Double>();
                    temp.put(slots.get(i), cost);
                    d.put(slots.get(j), temp);
                }
            }
        }
        return d;
    }

    public static Map<ExecutorDetails, Map<ExecutorDetails, Double>>  initializeW(List<ExecutorDetails> executors) {
        Map<ExecutorDetails, Map<ExecutorDetails, Double>> w = new HashMap<ExecutorDetails, Map<ExecutorDetails, Double>>();
        w.clear();
        for (int i = 0; i < executors.size(); i++) {
            for (int j = i; j < executors.size(); j++) {
                Random random = new Random();
                double cost = i == j ? 0.0 : random.nextDouble();
                if (w.containsKey(executors.get(i))) {
                    w.get(executors.get(i)).put(executors.get(j), cost);
                } else {
                    Map<ExecutorDetails, Double> temp = new HashMap<ExecutorDetails, Double>();
                    temp.put(executors.get(j), cost);
                    w.put(executors.get(i), temp);
                }
                if (w.containsKey(executors.get(j))) {
                    w.get(executors.get(j)).put(executors.get(i), cost);
                } else {
                    Map<ExecutorDetails, Double> temp = new HashMap<ExecutorDetails, Double>();
                    temp.put(executors.get(i), cost);
                    w.put(executors.get(j), temp);
                }
            }
        }
        return w;
    }

    public static  Map<ExecutorDetails, Double> initializeAlpha(TopologyDetails topology, List<ExecutorDetails> executors) {
        Map<ExecutorDetails, Double> alpha = new HashMap<>();

        alpha.clear();
        for (ExecutorDetails executor : executors) {
            Random random = new Random();
            double cost = random.nextDouble();
            alpha.put(executor, cost);
        }
        return alpha;
    }

    public static Map<ExecutorDetails, Map<ExecutorDetails, Double>> initializeBeta(TopologyDetails topology, List<ExecutorDetails> executors) {
        Map<ExecutorDetails, Map<ExecutorDetails, Double>> beta = new HashMap<>();
        beta.clear();
        for (int i = 0; i < executors.size(); i++) {
            for (int j = i; j < executors.size(); j++) {
                Random random = new Random();
                double cost = i == j ? 0.0 : random.nextDouble();
                if (beta.containsKey(executors.get(i))) {
                    beta.get(executors.get(i)).put(executors.get(j), cost);
                } else {
                    Map<ExecutorDetails, Double> temp = new HashMap<ExecutorDetails, Double>();
                    temp.put(executors.get(j), cost);
                    beta.put(executors.get(i), temp);
                }
                if (beta.containsKey(executors.get(j))) {
                    beta.get(executors.get(j)).put(executors.get(i), cost);
                } else {
                    Map<ExecutorDetails, Double> temp = new HashMap<ExecutorDetails, Double>();
                    temp.put(executors.get(i), cost);
                    beta.put(executors.get(j), temp);
                }

            }
        }
        return beta;
    }

    public static Map<ExecutorDetails, Map<ExecutorDetails, Double>>  initializeGamma(Cluster cluster, List<ExecutorDetails> executors) {
        Map<ExecutorDetails, Map<ExecutorDetails, Double>> gamma = new HashMap<>();
        gamma.clear();
        for (int i = 0; i < executors.size(); i++) {
            for (int j = i; j < executors.size(); j++) {
                double cost = W2 / cluster.getNetworkTopography().size();
                if (gamma.containsKey(executors.get(i))) {
                    gamma.get(executors.get(i)).put(executors.get(j), cost);
                } else {
                    Map<ExecutorDetails, Double> temp = new HashMap<ExecutorDetails, Double>();
                    temp.put(executors.get(j), cost);
                    gamma.put(executors.get(i), temp);
                }
                if (gamma.containsKey(executors.get(j))) {
                    gamma.get(executors.get(j)).put(executors.get(i), cost);
                } else {
                    Map<ExecutorDetails, Double> temp = new HashMap<ExecutorDetails, Double>();
                    temp.put(executors.get(i), cost);
                    gamma.put(executors.get(j), temp);
                }
            }
        }
        return gamma;
    }

/*
    public static double getProcessingCost(ExecutorDetails executor, WorkerSlot slot) {
        return alpha.get(executor) * lambda.get(slot);
    }

    public static double getTransferringCost(ExecutorDetails upExecutor, WorkerSlot upSlot, ExecutorDetails downExecutor, WorkerSlot downSlot) {
        return beta.get(upExecutor).get(downExecutor) * d.get(upSlot).get(downSlot) / 2;
    }

    public static double getRecoveryCost(ExecutorDetails upExecutor, ExecutorDetails downExecutor) {
        return gamma.get(upExecutor).get(downExecutor) * d.get(upExecutor).get(downExecutor) / 2;
    }
*/
    public static Map<ExecutorDetails, WorkerSlot> gameScheduling(TopologyDetails topology, Cluster cluster, List<ExecutorDetails> executors, List<WorkerSlot> slots) {
        //Initialize the network topology.
        Map<String, List<String>> networkTopography = cluster.getNetworkTopography();


       // for(Map.Entry<> s : networkTopography.entrySet())
        //        System.out.println("networkTopology  "+ s);


        Map<String, String> nodeToRack = new HashMap<String, String>();
        for (Map.Entry<String, List<String>> entry : networkTopography.entrySet()) {
            String rack = entry.getKey();
            List<String> nodes = entry.getValue();
            for (String node : nodes) {
                List<SupervisorDetails> supervisorsByHost = cluster.getSupervisorsByHost(node);
                nodeToRack.put(supervisorsByHost.get(0).getId(), rack);
            }
        }



        //The parameter q denotes the computation cost for executors to process a single tuple.
        Map<ExecutorDetails, Double> q = initializeQ(executors);

        //The parameter lambda denotes the data processing time for slots to process a single computation cost.
        Map<WorkerSlot, Double> lambda = initializeLambda(slots);

        //The parameter d denotes the data transferring time of node pairs.
        Map<WorkerSlot, Map<WorkerSlot, Double>> d = initializeD(slots);

        //The parameter w denotes the recover time of upstream and downstream executor pairs.
        Map<ExecutorDetails, Map<ExecutorDetails, Double>> w = initializeW(executors);

        //The parameter alpha is generated by transforming Problem 1 into Problem 2, and varies from executors to executors.
        Map<ExecutorDetails, Double> alpha = initializeAlpha(topology, executors);

        //The parameter beta is generated by transforming Problem 1 into Problem 2, and varies from executor pairs to executor pairs.
        Map<ExecutorDetails, Map<ExecutorDetails, Double>> beta = initializeBeta(topology, executors);

        //The parameter gamma is generated by transforming Problem 1 into Problem 2, and varies from executor pairs to executor pairs.
        Map<ExecutorDetails, Map<ExecutorDetails, Double>> gamma = initializeGamma(cluster, executors);

        //Initialize important parameters.


        //Assign an executor to a slot randomly.
        Map<ExecutorDetails, WorkerSlot> assignment = new HashMap<ExecutorDetails, WorkerSlot>();
        for (ExecutorDetails executor : executors) {
            Random random = new Random();
            int index = random.nextInt(slots.size());
            assignment.put(executor, slots.get(index));
        }

        //The flag indicates whether achieves Nash equilibrium.
        boolean isNashEquilibrium;

        //The process of best-response dynamics.
        do {
            isNashEquilibrium = true;
            //Make the best-response strategy for each executor by turn.
            for (ExecutorDetails executor : executors) {
                //Initialize the list of upstream and downstream executors for current executor.
                String currentComponentId = topology.getExecutorToComponent().get(executor);
                Component currentComponent = topology.getComponents().get(currentComponentId);
                if(currentComponent!=null){
                    List<ExecutorDetails> upstreamExecutors = new ArrayList<>();
                    System.out.println(currentComponent);
                    for (String parentId : currentComponent.parents) {
                        List<ExecutorDetails> parentExecutors = topology.getComponents().get(parentId).execs;
                        upstreamExecutors.addAll(parentExecutors);
                    }
                    upstreamExecutors.retainAll(executors);
                    List<ExecutorDetails> downstreamExecutors = new ArrayList<>();
                    for (String childrenId : currentComponent.children) {
                        downstreamExecutors.addAll(topology.getComponents().get(childrenId).execs);
                    }
                    downstreamExecutors.retainAll(executors);

                    //Store the previous assignment of an executor for later check of Nash equilibrium.
                    WorkerSlot preAssignment = assignment.get(executor);

                    //Initialize the costs of assigning an executor to different slots.
                    Map<WorkerSlot, Double> costExecutorToSlot = new HashMap<WorkerSlot, Double>();
                    for (WorkerSlot slot : slots) {
                        costExecutorToSlot.put(slot, alpha.get(executor) * lambda.get(slot));
                        for (ExecutorDetails upExecutor : upstreamExecutors) {
                            double transferringCost = costExecutorToSlot.get(slot) + beta.get(upExecutor).get(executor) * d.get(assignment.get(upExecutor)).get(slot) / 2;
                            costExecutorToSlot.put(slot, transferringCost);
                            if (nodeToRack.get(assignment.get(upExecutor).getNodeId()).equals(nodeToRack.get(assignment.get(executor).getNodeId()))) {
                                double recoveryCost = costExecutorToSlot.get(slot) + gamma.get(upExecutor).get(executor) * w.get(upExecutor).get(executor) / 2;
                                costExecutorToSlot.put(slot, recoveryCost);
                            }
                        }
                        for (ExecutorDetails downExecutor : downstreamExecutors) {
                            //                                                        beta.get(upExecutor).get(downExecutor) * d.get(upSlot).get(downSlot) / 2
                            double transferringCost = costExecutorToSlot.get(slot) + beta.get(executor).get(downExecutor) * d.get(slot).get(assignment.get(downExecutor)) / 2;
                            costExecutorToSlot.put(slot, transferringCost);
                            System.out.println("nodeToRack size : " + nodeToRack.size() + "     assignment size : " + assignment.size());
                            System.out.println(nodeToRack.get(assignment.get(executor).getNodeId()).equals(nodeToRack.get(assignment.get(downExecutor).getNodeId())));

                            if (nodeToRack.get(assignment.get(executor).getNodeId()).equals(nodeToRack.get(assignment.get(downExecutor).getNodeId()))) {
                                double recoveryCost = costExecutorToSlot.get(slot) + gamma.get(executor).get(downExecutor) * w.get(executor).get(downExecutor) / 2;
                                costExecutorToSlot.put(slot, recoveryCost);
                            }
                        }
                    }

                    //Make the best-response strategy for an executor.
                    double minCost = Double.MAX_VALUE;
                    for (Map.Entry<WorkerSlot, Double> entry : costExecutorToSlot.entrySet()) {
                        if (entry.getValue() < minCost) {
                            minCost = entry.getValue();
                            assignment.put(executor, entry.getKey());
                        }
                    }

                    //Check whether achieves Nash equilibrium.
                    if (isNashEquilibrium && assignment.get(executor) != preAssignment) {
                        isNashEquilibrium = false;
                    }
                }
            }
        } while (!isNashEquilibrium);

        return assignment;
    }

    public static Map<WorkerSlot, List<ExecutorDetails>> getAliveAssignedWorkerSlotExecutors(Cluster cluster, String topologyId) {
        SchedulerAssignment existingAssignment = cluster.getAssignmentById(topologyId);
        Map<ExecutorDetails, WorkerSlot> executorToSlot = null;
        if (existingAssignment != null) {
            executorToSlot = existingAssignment.getExecutorToSlot();
        }

        return AresUtils.reverseMap(executorToSlot);
    }

    private static Map<ExecutorDetails, WorkerSlot> scheduleTopologyWithGame(TopologyDetails topology, Cluster cluster) {
        List<WorkerSlot> availableSlots = cluster.getAvailableSlots();
        Set<ExecutorDetails> allExecutors = (Set<ExecutorDetails>) topology.getExecutors();
        Map<WorkerSlot, List<ExecutorDetails>> aliveAssigned = getAliveAssignedWorkerSlotExecutors(cluster, topology.getId());
        int totalSlotsToUse = Math.min(topology.getNumWorkers(), availableSlots.size() + aliveAssigned.size());

        if (availableSlots == null) {
            LOG.error("No available slots for topology: {}", topology.getName());
            return new HashMap<ExecutorDetails, WorkerSlot>();
        }

        //allow requesting slots number bigger than available slots
        int toIndex = (totalSlotsToUse - aliveAssigned.size())
                > availableSlots.size() ? availableSlots.size() : (totalSlotsToUse - aliveAssigned.size());
        List<WorkerSlot> reassignSlots = availableSlots.subList(0, toIndex);

        Set<ExecutorDetails> aliveExecutors = new HashSet<ExecutorDetails>();
        for (List<ExecutorDetails> list : aliveAssigned.values()) {
            aliveExecutors.addAll(list);
        }
        Set<ExecutorDetails> reassignExecutors = Sets.difference(allExecutors, aliveExecutors);

        Map<ExecutorDetails, WorkerSlot> reassignment = new HashMap<ExecutorDetails, WorkerSlot>();
        if (reassignSlots.size() == 0) {
            return reassignment;
        }

        List<ExecutorDetails> executors = new ArrayList<ExecutorDetails>(reassignExecutors);

        System.out.println("slots size:"+ reassignSlots.size());
        reassignment = gameScheduling(topology, cluster, executors, reassignSlots);

        if (reassignment.size() != 0) {
            LOG.info("Available slots: {}", availableSlots.toString());
        }
        return reassignment;
    }

    public static void scheduleTopologiesWithGame(Topologies topologies, Cluster cluster) {

        LOG.info("start GameScheduler................................\n");
        for (TopologyDetails topology : cluster.needsSchedulingTopologies(topologies)) {
            String topologyId = topology.getId();
            Map<ExecutorDetails, WorkerSlot> newAssignment = scheduleTopologyWithGame(topology, cluster);
            Map<WorkerSlot, List<ExecutorDetails>> nodePortToExecutors = AresUtils.reverseMap(newAssignment);

            for (Map.Entry<WorkerSlot, List<ExecutorDetails>> entry : nodePortToExecutors.entrySet()) {
                WorkerSlot nodePort = entry.getKey();
                List<ExecutorDetails> executors = entry.getValue();
                cluster.assign(nodePort, topologyId, executors);
            }
        }
    }

    @Override
    public void prepare(Map conf) {

    }

    @Override
    public void schedule(Topologies topologies, Cluster cluster) {
        scheduleTopologiesWithGame(topologies, cluster);
    }


    public Map<String, Object> config() {
        return new HashMap<>();
    }

}