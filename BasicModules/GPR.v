module GPR (
    input clk,
    input reset,
    input [31:0] PC,
    input [4:0] A1,
    input [4:0] A2,
    input [4:0] A3,
    output [31:0] RD1,
    output [31:0] RD2,
    input [31:0] WD,
    input RFWr
);
    reg [31:0] register[31:0];

    assign RD1=register[A1];
    assign RD2=register[A2];

    integer i;
    initial begin
        for(i=0;i<32;i=i+1)
        begin
            register[i]<=0;
        end
    end

    always @(posedge clk) begin
        if(reset)
        begin
            for(i=0;i<32;i=i+1)
            begin
                register[i]<=0;
            end
        end
        else if(RFWr&&A3!=0)
        begin
            register[A3]<=WD;
        end
    end
endmodule